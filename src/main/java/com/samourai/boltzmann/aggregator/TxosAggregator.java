package com.samourai.boltzmann.aggregator;

import com.samourai.boltzmann.beans.Txos;
import com.samourai.boltzmann.linker.IntraFees;
import com.samourai.boltzmann.utils.ListsUtils;
import com.samourai.boltzmann.utils.Utils;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;
import java8.util.function.Consumer;
import java8.util.function.IntConsumer;
import java8.util.stream.IntStreams;
import java8.util.stream.LongStreams;
import java8.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TxosAggregator {
  private static final Logger log = LoggerFactory.getLogger(TxosAggregator.class);

  public TxosAggregator() {}

  /**
   * Matches input/output aggregates by values and returns a bunch of data structs
   *
   * @param allAgg
   * @param fees
   * @param intraFees
   * @return
   */
  public TxosAggregatesMatches matchAggByVal(
      TxosAggregates allAgg, long fees, IntraFees intraFees) {
    long[] allInAggVal = allAgg.getInAgg().getAllAggVal();
    long[] allOutAggVal = allAgg.getOutAgg().getAllAggVal();

    // Gets unique values of input / output aggregates sorted ASC
    long[] allUniqueInAggVal = LongStreams.of(allInAggVal).distinct().sorted().toArray();
    long[] allUniqueOutAggVal = LongStreams.of(allOutAggVal).distinct().sorted().toArray();

    if (log.isDebugEnabled()) {
      Utils.logMemory(
          "Matching aggregates: " + allUniqueOutAggVal.length + "x" + allUniqueInAggVal.length);
    }

    List<Integer> allMatchInAgg = new ArrayList<Integer>();
    Map<Integer, Long> matchInAggToVal = new HashMap<Integer, Long>();
    Map<Long, List<Integer>> valToMatchOutAgg = new LinkedHashMap<Long, List<Integer>>();

    // Computes total fees paid/receiver by taker/maker
    long feesTaker = 0, feesMaker = 0;
    boolean hasIntraFees = intraFees != null && intraFees.hasFees();
    if (hasIntraFees) {
      feesTaker = fees + intraFees.getFeesTaker();
      feesMaker = -intraFees.getFeesMaker(); // doesn 't take into account tx fees paid by makers
    }

    // Finds input and output aggregates with matching values
    for (int i = 0; i < allUniqueInAggVal.length; i++) {
      long inAggVal = allUniqueInAggVal[i];
      for (int j = 0; j < allUniqueOutAggVal.length; j++) {
        long outAggVal = allUniqueOutAggVal[j];

        long diff = inAggVal - outAggVal;

        if (!hasIntraFees && diff < 0) {
          break;
        } else {
          // Computes conditions required for a matching
          boolean condNoIntrafees = (!hasIntraFees && diff <= fees);
          boolean condIntraFees =
              (hasIntraFees
                  && ((diff <= 0 && diff >= feesMaker) || (diff >= 0 && diff <= feesTaker)));

          if (condNoIntrafees || condIntraFees) {
            // Registers the matching input aggregate
            for (int inIdx = 0; inIdx < allInAggVal.length; inIdx++) {
              if (allInAggVal[inIdx] == inAggVal) {
                if (!allMatchInAgg.contains(inIdx)) {
                  allMatchInAgg.add(inIdx);
                  matchInAggToVal.put(inIdx, inAggVal);
                }
              }
            }

            // Registers the matching output aggregate
            List<Integer> keysMatchOutAgg = new ArrayList<Integer>();
            for (int indice = 0; indice < allOutAggVal.length; indice++) {
              if (allOutAggVal[indice] == outAggVal) {
                keysMatchOutAgg.add(indice);
              }
            }
            if (!valToMatchOutAgg.containsKey(inAggVal)) {
              valToMatchOutAgg.put(inAggVal, new ArrayList<Integer>());
            }
            valToMatchOutAgg.get(inAggVal).addAll(keysMatchOutAgg);
          }
        }
      }
      if (i % 400 == 0) {
        if (log.isDebugEnabled()) {
          Utils.logMemory(i + "/" + allUniqueInAggVal.length);
        }
      }
    }
    if (log.isDebugEnabled()) {
      Utils.logMemory();
    }
    return new TxosAggregatesMatches(allMatchInAgg, matchInAggToVal, valToMatchOutAgg);
  }

  /**
   * Computes a matrix of valid combinations (pairs) of input aggregates Returns a dictionary
   * (parent_agg => (child_agg1, child_agg2)) We have a valid combination (agg1, agg2) if: R1/
   * child_agg1 & child_agg2 = 0 (no bitwise overlap) R2/ child_agg1 > child_agg2 (matrix is
   * symmetric)
   */
  public Map<Long, List<int[]>> computeInAggCmbn(TxosAggregatesMatches aggMatches) {
    // aggs = allMatchInAgg[1:-1]
    final LinkedList<Integer> aggs = new LinkedList<Integer>(aggMatches.getAllMatchInAgg());
    if (log.isDebugEnabled()) {
      Utils.logMemory("Computing combinations: " + aggs.size());
    }

    aggs.pollFirst(); // remove 0

    final Map<Long, List<int[]>> mat = new LinkedHashMap<Long, List<int[]>>();
    if (!aggs.isEmpty()) {
      final int tgt = aggs.pollLast();

      IntStreams.range(0, tgt + 1)
          .parallel()
          .forEachOrdered(
              new IntConsumer() {
                @Override
                public void accept(final int i) {
                  if (aggs.contains(i)) {
                    int jMax = Math.min(i, tgt - i + 1);

                    IntStreams.range(0, jMax)
                        .parallel()
                        .forEach(
                            new IntConsumer() {
                              @Override
                              public void accept(final int j) {
                                if ((i & j) == 0 && aggs.contains(j)) {
                                  List<int[]> aggChilds =
                                      createLine(mat, (long) i + j); // synchronized line creation
                                  aggChilds.add(new int[] {i, j});
                                }
                              }
                            });
                  }
                  if (i % 400 == 0) {
                    if (log.isDebugEnabled()) {
                      Utils.logMemory(i + "/" + tgt + "... " + mat.size() + " matches found");
                    }
                  }
                }
              });
    }
    if (log.isDebugEnabled()) {
      Utils.logMemory(mat.size() + " combinations");
    }
    return mat;
  }

  private synchronized List<int[]> createLine(Map<Long, List<int[]>> mat, long i) {
    List<int[]> line = mat.get(i);
    if (mat.get(i) == null) {
      line = new ArrayList<int[]>();
      mat.put(i, line);
    }
    return line;
  }

  /**
   * Checks the existence of deterministic links between inputs and outputs
   *
   * @return list of deterministic links as tuples (idx_output, idx_input)
   */
  public Set<long[]> checkDtrmLinks(
      Txos txos, TxosAggregates allAgg, TxosAggregatesMatches aggMatches) {
    int nbIns = txos.getInputs().size();
    int nbOuts = txos.getOutputs().size();

    if (log.isDebugEnabled()) {
      Utils.logMemory("Checking deterministic links: " + nbOuts + "x" + nbIns);
    }

    ObjectBigList<IntBigList> matCmbn = ListsUtils.newIntMatrix(nbOuts, nbIns, 0);

    IntBigList inCmbn = ListsUtils.newIntBigList(nbIns, 0);
    for (Map.Entry<Integer, Long> inEntry : aggMatches.getMatchInAggToVal().entrySet()) {
      int inIdx = inEntry.getKey();
      long val = inEntry.getValue();
      for (int outIdx : aggMatches.getValToMatchOutAgg().get(val)) {
        // Computes a matrix storing numbers of raw combinations matching input/output pairs
        updateLinkCmbn(matCmbn, inIdx, outIdx, allAgg);

        // Computes sum of combinations along inputs axis to get the number of combinations
        long[] inIndexes = allAgg.getInAgg().getAllAggIndexes().get(inIdx);
        for (long inIndex : inIndexes) {
          int currentValue = inCmbn.getInt(inIndex);
          inCmbn.set(inIndex, currentValue + 1);
        }
      }
    }

    // Builds a list of sets storing inputs having a deterministic link with an output
    int nbCmbn = inCmbn.getInt(0);
    Set<long[]> dtrmCoords = findDtrmLinks(matCmbn, nbCmbn);
    if (log.isDebugEnabled()) {
      Utils.logMemory(dtrmCoords.size() + " deterministic links found");
    }
    return dtrmCoords;
  }

  private class ComputeLinkMatrixTask {
    private int idxIl;
    private long il;
    private long ir;
    private Map<Long, Map<Long, int[]>> dOut;

    public ComputeLinkMatrixTask(int idxIl, long il, long ir, Map<Long, Map<Long, int[]>> dOut) {
      this.idxIl = idxIl;
      this.il = il;
      this.ir = ir;
      this.dOut = dOut;
    }

    public int getIdxIl() {
      return idxIl;
    }

    public void setIdxIl(int idxIl) {
      this.idxIl = idxIl;
    }

    public long getIl() {
      return il;
    }

    public long getIr() {
      return ir;
    }

    public Map<Long, Map<Long, int[]>> getdOut() {
      return dOut;
    }
  }

  /**
   * Computes the linkability matrix Returns the number of possible combinations and the links
   * matrix Implements a depth-first traversal of the inputs combinations tree (right to left) For
   * each input combination we compute the matching output combinations. This is a basic brute-force
   * solution. Will have to find a better method later.
   *
   * @param maxDuration in seconds
   */
  public TxosAggregatorResult computeLinkMatrix(
      Txos txos,
      TxosAggregates allAgg,
      TxosAggregatesMatches aggMatches,
      Map<Long, List<int[]>> matInAggCmbn,
      Integer maxDuration) {
    int nbTxCmbn = 0;
    long itGt = (long) Math.pow(2, txos.getInputs().size()) - 1;
    long otGt = (long) Math.pow(2, txos.getOutputs().size()) - 1;

    long estIters = (long) 1.5 * itGt * itGt / 32;
    if (log.isDebugEnabled()) {
      Utils.logMemory(
          "Computing link matrix: " + itGt + "x" + otGt + " (" + estIters + " iters est.)");
    }

    final Map<long[], Integer> dLinks = new LinkedHashMap<long[], Integer>();

    // Initializes a stack of tasks & sets the initial task
    //  0: index used to resume the processing of the task (required for depth-first algorithm)
    //  1: il = left input aggregate
    //  2: ir = right input aggregate
    //  3: d_out = outputs combination matching with current input combination
    //             dictionary of dictionary :  { or =>  { ol => (nb_parents_cmbn, nb_children_cmbn)
    // } }

    Deque<ComputeLinkMatrixTask> stack = new LinkedList<ComputeLinkMatrixTask>();

    // ini_d_out[otgt] = { 0: (1, 0) }
    Map<Long, Map<Long, int[]>> dOutInitial = new LinkedHashMap<Long, Map<Long, int[]>>();
    Map<Long, int[]> dOutEntry = new LinkedHashMap<Long, int[]>();
    dOutEntry.put(0L, new int[] {1, 0});
    dOutInitial.put(otGt, dOutEntry);

    stack.add(new ComputeLinkMatrixTask(0, 0, itGt, dOutInitial));

    // Sets start date/hour
    long startTime = System.currentTimeMillis();

    int iteration = 0;
    // Iterates over all valid inputs combinations (top->down)
    while (!stack.isEmpty()) {
      // Checks duration
      long currTime = System.currentTimeMillis();
      long deltaTimeSeconds = (currTime - startTime) / 1000;
      if (maxDuration != null && deltaTimeSeconds >= maxDuration) {
        System.out.println("maxDuration limit reached!");
        return new TxosAggregatorResult(0, null);
      }

      // Gets data from task
      ComputeLinkMatrixTask t = stack.getLast();
      int nIdxIl = t.getIdxIl();

      // Gets all valid decompositions of right input aggregate
      List<int[]> ircs = matInAggCmbn.get(t.getIr());
      int lenIrcs = (ircs != null ? ircs.size() : 0);

      for (int i = t.getIdxIl(); i < lenIrcs; i++) {
        nIdxIl = i;

        // Gets left input sub-aggregate (column from ircs)
        int nIl = ircs.get(i)[1];

        // Checks if we must process this pair (columns from ircs are sorted in decreasing order)
        if (nIl > t.getIl()) {
          // Gets the right input sub-aggregate (row from ircs)
          int nIr = ircs.get(i)[0];

          if (iteration % 50 == 0) {
            if (log.isDebugEnabled()) {
              Utils.logMemory(
                  "Computing link matrix... Task "
                      + iteration
                      + "/"
                      + estIters
                      + " est. ("
                      + t.getdOut().size()
                      + " cmbn)");
            }
          }

          // Run task
          Map<Long, Map<Long, int[]>> ndOut = runTask(nIl, nIr, aggMatches, otGt, t.getdOut());

          // Updates idx_il for the current task
          t.setIdxIl(i + 1);

          // Pushes a new task which will decompose the right input aggregate
          stack.add(new ComputeLinkMatrixTask(0, nIl, nIr, ndOut));

          // Executes the new task (depth-first)
          break;
        } else {
          // No more results for il, triggers a break and a pop
          nIdxIl = ircs.size();
          break;
        }
      }
      iteration++;

      // Checks if task has completed
      if (nIdxIl > (lenIrcs - 1)) {
        // Pops the current task
        t = stack.removeLast();

        // Checks if it's the root task
        if (stack.isEmpty()) {
          // Retrieves the number of combinations from root task
          nbTxCmbn = t.getdOut().get(otGt).get(0L)[1];
        } else {
          // Gets parent task
          final ComputeLinkMatrixTask pt = stack.getLast();

          // Iterates over all entries from d_out
          final long il = t.getIl();
          final long ir = t.getIr();
          StreamSupport.stream(t.getdOut().entrySet()).forEach(new Consumer<Map.Entry<Long, Map<Long, int[]>>>() {
            @Override
            public void accept(Map.Entry<Long, Map<Long, int[]>> doutEntry) {
              final long or = doutEntry.getKey();
              Map<Long, int[]> lOl = doutEntry.getValue();
              final long[] rKey = new long[]{ir, or};

              // Iterates over all left aggregates
              StreamSupport.stream(lOl.entrySet()).parallel().forEachOrdered(new Consumer<Map.Entry<Long, int[]>>() {
                @Override
                public void accept(Map.Entry<Long, int[]> olEntry) {
                  long ol = olEntry.getKey();
                  int nbPrnt = olEntry.getValue()[0];
                  int nbChld = olEntry.getValue()[1];

                  long[] lKey = new long[]{il, ol};

                  // Updates the dictionary of links for the pair of aggregates
                  final int nbOccur = nbChld + 1;
                  dLinks.put(rKey, (dLinks.containsKey(rKey) ? dLinks.get(rKey) : 0) + nbPrnt);
                  dLinks.put(
                          lKey, (dLinks.containsKey(lKey) ? dLinks.get(lKey) : 0) + nbPrnt * nbOccur);

                  // Updates parent d_out by back-propagating number of child combinations
                  final long pOr = ol + or;
                  final Map<Long, int[]> plOl = pt.getdOut().get(pOr);
                  StreamSupport.stream(plOl.entrySet()).parallel().forEach(
                          new java8.util.function.Consumer<Map.Entry<Long, int[]>>() {
                            @Override
                            public void accept(Map.Entry<Long, int[]> plOlEntry) {
                              plOlEntry.getValue()[1] += nbOccur;
                            }
                          }
                  );
                }
              });
            }
          });
        }
      }
    }
    if (log.isDebugEnabled()) {
      Utils.logMemory(
          "Computing link matrix DONE... (" + iteration + " iterations/" + estIters + " est.)");
    }

    // Fills the matrix
    ObjectBigList<IntBigList> links = newLinkCmbn(allAgg);
    updateLinkCmbn(links, itGt, otGt, allAgg);
    nbTxCmbn++;
    for (Map.Entry<long[], Integer> linkEntry : dLinks.entrySet()) {
      long[] link = linkEntry.getKey();
      int mult = linkEntry.getValue();
      ObjectBigList<IntBigList> linkCmbn = newLinkCmbn(allAgg);
      updateLinkCmbn(linkCmbn, link[0], link[1], allAgg);
      for (long i = 0; i < links.size64(); i++) {
        for (long j = 0; j < links.get(i).size64(); j++) {
          int currentValue = links.get(i).getInt(j);
          links.get(i).set(j, currentValue + linkCmbn.get(i).getInt(j) * mult);
        }
      }
    }
    return new TxosAggregatorResult(nbTxCmbn, links);
  }

  private Map<Long, Map<Long, int[]>> runTask(
      final int nIl,
      final int nIr,
      final TxosAggregatesMatches aggMatches,
      final long otGt,
      final Map<Long, Map<Long, int[]>> dOut) {
    final Map<Long, Map<Long, int[]>> ndOut = new LinkedHashMap<Long, Map<Long, int[]>>();

    // Iterates over outputs combinations previously found
    StreamSupport.stream(dOut.entrySet()).parallel().forEachOrdered(
            new java8.util.function.Consumer<Map.Entry<Long, Map<Long, int[]>>>() {
              @Override
              public void accept(Map.Entry<Long, Map<Long, int[]>> oREntry) {
                final long oR = oREntry.getKey();
                final long sol = otGt - oR;

                // Computes the number of parent combinations
                int nbPrt = 0;
                for (int[] s : oREntry.getValue().values()) {
                  nbPrt += s[0];
                }
                final int myNbPrt = nbPrt;

                // Iterates over output sub-aggregates matching with left input sub-aggregate
                long valIl = aggMatches.getMatchInAggToVal().get(nIl);
                StreamSupport.stream(aggMatches.getValToMatchOutAgg().get(valIl)).parallel().forEach(new Consumer<Integer>() {
                  @Override
                  public void accept(Integer nOl) {
                    // Checks compatibility of output sub-aggregate with left part of output combination
                    if ((sol & nOl) == 0) {
                      // Computes:
                      //   the sum corresponding to the left part of the output combination
                      //   the complementary right output sub-aggregate
                      long nSol = sol + nOl;
                      long nOr = otGt - nSol;

                      // Checks if the right output sub-aggregate is valid
                      long valIr = aggMatches.getMatchInAggToVal().get(nIr);
                      List<Integer> matchOutAgg = aggMatches.getValToMatchOutAgg().get(valIr);

                      // Adds this output combination into n_d_out if all conditions met
                      if ((nSol & nOr) == 0 && matchOutAgg.contains((int) nOr)) { // TODO !!! CAST
                        Map<Long, int[]> ndOutVal = ndOutLine(ndOut, nOr); // synchronized
                        ndOutVal.put((long)nOl, new int[] {myNbPrt, 0});
                      }
                    }
                  }
                });
              }
            }
    );
    return ndOut;
  }

  private synchronized Map<Long, int[]> ndOutLine(Map<Long, Map<Long, int[]>> ndOut, long idx) {
    Map<Long, int[]> ndOutVal = ndOut.get(idx);
    if (ndOutVal == null) {
      ndOutVal = new LinkedHashMap<Long, int[]>();
      ndOut.put(idx, ndOutVal);
    }
    return ndOutVal;
  }

  /**
   * Creates a new linkability matrix.
   *
   * @param allAgg
   */
  private ObjectBigList<IntBigList> newLinkCmbn(TxosAggregates allAgg) {
    long maxOutIndex = 0;
    for (long[] indexes : allAgg.getOutAgg().getAllAggIndexes()) {
      long max = LongStreams.of(indexes).max().orElse(0);
      if (max > maxOutIndex) {
        maxOutIndex = max;
      }
    }

    long maxInIndex = 0;
    for (long[] indexes : allAgg.getInAgg().getAllAggIndexes()) {
      long max = LongStreams.of(indexes).max().orElse(0);
      if (max > maxInIndex) {
        maxInIndex = max;
      }
    }

    // System.err.println("outAgg="+Arrays.deepToString(allAgg.getOutAgg().getAllAggIndexes().values().toArray()));
    // System.err.println("inAgg="+Arrays.deepToString(allAgg.getInAgg().getAllAggIndexes().values().toArray()));
    // System.err.println("maxOutIndex="+maxOutIndex+", maxInIndex="+maxInIndex);
    ObjectBigList<IntBigList> matCmbn = ListsUtils.newIntMatrix(maxOutIndex + 1, maxInIndex + 1, 0);
    return matCmbn;
  }

  /**
   * Updates the linkability matrix for aggregate designated by inAgg/outAgg.
   *
   * @param matCmbn linkability matrix
   * @param inAgg input aggregate
   * @param outAgg output aggregate
   * @param allAgg
   */
  private ObjectBigList<IntBigList> updateLinkCmbn(
      ObjectBigList<IntBigList> matCmbn, long inAgg, long outAgg, TxosAggregates allAgg) {
    long[] outIndexes = allAgg.getOutAgg().getAllAggIndexes().get(outAgg);
    long[] inIndexes = allAgg.getInAgg().getAllAggIndexes().get(inAgg);

    for (long inIndex : inIndexes) {
      for (long outIndex : outIndexes) {
        int value = matCmbn.get(outIndex).getInt(inIndex);
        matCmbn.get(outIndex).set(inIndex, value + 1);
      }
    }
    return matCmbn;
  }

  /**
   * Builds a list of sets storing inputs having a deterministic link with an output
   *
   * @param matCmbn linkability matrix
   * @param nbCmbn number of combination
   * @return
   */
  public Set<long[]> findDtrmLinks(ObjectBigList<IntBigList> matCmbn, int nbCmbn) {
    Set<long[]> dtrmCoords = new LinkedHashSet<long[]>();
    for (long i = 0; i < matCmbn.size64(); i++) {
      for (long j = 0; j < matCmbn.get(i).size64(); j++) {
        if (matCmbn.get(i).getInt(j) == nbCmbn) {
          dtrmCoords.add(new long[] {i, j});
        }
      }
    }
    return dtrmCoords;
  }
}
