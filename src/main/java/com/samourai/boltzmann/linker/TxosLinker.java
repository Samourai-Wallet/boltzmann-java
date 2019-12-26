package com.samourai.boltzmann.linker;

import com.samourai.boltzmann.aggregator.*;
import com.samourai.boltzmann.beans.Txos;
import com.samourai.boltzmann.processor.TxProcessorConst;
import com.samourai.boltzmann.utils.ListsUtils;
import com.samourai.boltzmann.utils.Utils;
import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;
import java.util.*;
import java.util.Map.Entry;
import java8.util.function.Consumer;
import java8.util.function.LongUnaryOperator;
import java8.util.stream.LongStreams;
import java8.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Compute the entropy and the inputs/outputs linkability of a of Bitcoin transaction. */
public class TxosLinker {
  private static final Logger log = LoggerFactory.getLogger(TxosLinker.class);

  // Default maximum duration in seconds
  private static final int MAX_DURATION = 180;

  // Max number of inputs (or outputs) which can be processed by this algorithm
  private static final int MAX_NB_TXOS = 12;

  // Markers
  private static final String MARKER_FEES = "FEES";
  private static final String MARKER_PACK = "PACK_I";

  // fees associated to the transaction
  private long feesOrig;
  private long fees;

  private List<Pack> packs = new ArrayList<Pack>();

  // max number of txos. Txs with more than max_txos inputs or outputs are not processed.
  Integer maxTxos;

  // Maximum duration of the script (in seconds)
  Integer maxDuration = MAX_DURATION;

  /**
   * Constructor.
   *
   * @param fees amount of fees associated to the transaction
   * @param maxDuration max duration allocated to processing of a single tx (in seconds)
   * @param maxTxos max number of txos. Txs with more than max_txos inputs or outputs are not
   *     processed.
   */
  public TxosLinker(long fees, Integer maxDuration, Integer maxTxos) {
    this.feesOrig = fees;
    this.maxDuration = maxDuration;
    this.maxTxos = maxTxos;
  }

  /**
   * Computes the linkability between a set of input txos and a set of output txos.
   *
   * @param txos list of inputs/ouputs txos [(v1_id, v1_amount), ...]
   * @param linkedTxos list of sets storing linked input txos. Each txo is identified by its id
   * @param options actions to be applied
   * @param intraFees tuple (fees_maker, fees_taker) of max "fees" paid among participants used for
   *     joinmarket transactions fees_maker are potential max "fees" received by a participant from
   *     another participant fees_taker are potential max "fees" paid by a participant to all others
   *     participants
   * @return
   */
  public TxosLinkerResult process(
      Txos txos,
      Collection<Set<String>> linkedTxos,
      Set<TxosLinkerOptionEnum> options,
      IntraFees intraFees) {
    // Packs txos known as being controlled by a same entity
    // It decreases the entropy and speeds-up computations
    if (linkedTxos != null && !linkedTxos.isEmpty()) {
      txos = packLinkedTxos(linkedTxos, txos); // TODO test
    }

    // Manages fees
    if (options.contains(TxosLinkerOptionEnum.MERGE_FEES) && this.feesOrig > 0) {
      // Manages fees as an additional output (case of sharedsend by blockchain.info).
      // Allows to reduce the volume of computations to be done.
      this.fees = 0;
      txos.getOutputs().put(MARKER_FEES, this.feesOrig);
    } else {
      this.fees = this.feesOrig;
    }

    TxosAggregator aggregator = new TxosAggregator();

    int nbOuts = txos.getOutputs().size();
    int nbIns = txos.getInputs().size();
    boolean hasIntraFees = intraFees != null && intraFees.hasFees();

    // Checks deterministic links
    int nbCmbn = 0;
    ObjectBigList<IntBigList> matLnk = ListsUtils.newIntMatrix(nbOuts, nbIns, 0);

    // Prepares the data
    TxosAggregates allAgg = prepareData(txos);
    txos = new Txos(allAgg.getInAgg().getTxos(), allAgg.getOutAgg().getTxos());
    TxosAggregatesMatches aggMatches = aggregator.matchAggByVal(allAgg, fees, intraFees);

    Set<long[]> dtrmLnks = new LinkedHashSet<long[]>();
    if (options.contains(TxosLinkerOptionEnum.PRECHECK)
        && this.checkLimitOk(txos)
        && !hasIntraFees) {
      if (log.isDebugEnabled()) {
        Utils.logMemory("# PRECHECK");
      }

      // Checks deterministic links
      dtrmLnks = aggregator.checkDtrmLinks(txos, allAgg, aggMatches);

      // If deterministic links have been found, fills the linkability matrix
      // (returned as result if linkability is not processed)
      if (!dtrmLnks.isEmpty()) {
        final ObjectBigList<IntBigList> matLnkFinal = matLnk;

        StreamSupport.stream(dtrmLnks)
            .parallel()
            .forEach(
                new Consumer<long[]>() {
                  @Override
                  public void accept(long[] dtrmLnk) {
                    matLnkFinal.get(dtrmLnk[0]).set(dtrmLnk[1], 1);
                  }
                });
      }
    }

    // Checks if all inputs and outputs have already been merged
    if (nbIns == 0 || nbOuts == 0) {
      nbCmbn = 1;
      for (IntBigList line : matLnk) {
        ListsUtils.fill(line, 1, line.size64());
      }
    } else if (options.contains(TxosLinkerOptionEnum.LINKABILITY) && this.checkLimitOk(txos)) {
      if (log.isDebugEnabled()) {
        Utils.logMemory("# LINKABILITY");
      }

      // Packs deterministic links if needed
      if (!dtrmLnks.isEmpty()) {
        Utils.logMemory("PACK " + dtrmLnks.size() + " deterministic links");
        final Txos txosFinal = txos;
        List<Set<String>> dtrmCoordsList = new ArrayList<Set<String>>();
        for (long[] array : dtrmLnks) {
          Set<String> set = new LinkedHashSet<String>();
          set.add("" + txosFinal.getOutputs().keySet().toArray()[(int) array[0]]); // TODO !!! cast
          set.add("" + txosFinal.getInputs().keySet().toArray()[(int) array[1]]); // TODO !!! cast
          dtrmCoordsList.add(set);
        }
        txos = packLinkedTxos(dtrmCoordsList, txos);

        // txos changed, recompute allAgg
        allAgg = prepareData(txos);
        txos = new Txos(allAgg.getInAgg().getTxos(), allAgg.getOutAgg().getTxos());
        aggMatches = aggregator.matchAggByVal(allAgg, fees, intraFees);
      }

      // Computes a matrix storing a tree composed of valid pairs of input aggregates
      Map<Long, List<int[]>> matInAggCmbn = aggregator.computeInAggCmbn(aggMatches);

      // Builds the linkability matrix
      TxosAggregatorResult result =
          aggregator.computeLinkMatrix(txos, allAgg, aggMatches, matInAggCmbn, maxDuration);
      nbCmbn = result.getNbCmbn();
      matLnk = result.getMatLnkCombinations();

      // Refresh deterministical links
      dtrmLnks = aggregator.findDtrmLinks(matLnk, nbCmbn);
    }

    if (!packs.isEmpty()) {
      if (log.isDebugEnabled()) {
        Utils.logMemory("# UNPACK " + packs.size() + " packs");
      }
      // Unpacks the matrix
      UnpackLinkMatrixResult unpackResult = unpackLinkMatrix(matLnk, txos);
      txos = unpackResult.getTxos();
      matLnk = unpackResult.getMatLnk();

      // Refresh deterministical links // TODO reintegrate in unpackLinkMatrix?
      dtrmLnks = aggregator.findDtrmLinks(matLnk, nbCmbn);
    }

    return new TxosLinkerResult(nbCmbn, matLnk, dtrmLnks, txos);
  }

  /**
   * Packs input txos which are known as being controlled by a same entity
   *
   * @param linkedTxos list of sets storing linked input txos. Each txo is identified by its "id"
   * @return Txos
   */
  protected Txos packLinkedTxos(Collection<Set<String>> linkedTxos, Txos txos) {
    int idx = packs.size();

    Txos packedTxos =
        new Txos(
            new LinkedHashMap<String, Long>(txos.getInputs()),
            new LinkedHashMap<String, Long>(txos.getOutputs()));

    // Merges packs sharing common elements
    List<Set<String>> newPacks = ListsUtils.mergeSets(linkedTxos);

    for (Set<String> pack : newPacks) {
      List<Entry<String, Long>> ins = new ArrayList<Entry<String, Long>>();
      long valIns = 0;

      for (String inPack : pack) {
        if (inPack.startsWith(TxProcessorConst.MARKER_INPUT)) {
          long inPackValue = packedTxos.getInputs().get(inPack);
          ins.add(new AbstractMap.SimpleEntry<String, Long>(inPack, inPackValue));
          valIns += packedTxos.getInputs().get(inPack);
          packedTxos.getInputs().remove(inPack);
        }
      }
      idx++;

      if (!ins.isEmpty()) {
        String lbl = MARKER_PACK + idx;
        packedTxos.getInputs().put(lbl, valIns);
        packs.add(new Pack(lbl, PackType.INPUTS, ins, new ArrayList<String>()));
      }
    }
    return packedTxos;
  }

  /**
   * Unpacks linked txos in the linkability matrix.
   *
   * @param matLnk linkability matrix to be unpacked
   * @param txos packed txos containing the pack
   * @return UnpackLinkMatrixResult
   */
  protected UnpackLinkMatrixResult unpackLinkMatrix(ObjectBigList<IntBigList> matLnk, Txos txos) {
    ObjectBigList<IntBigList> matRes = new ObjectBigArrayBigList<IntBigList>(matLnk);
    Txos newTxos =
        new Txos(
            new LinkedHashMap<String, Long>(txos.getInputs()),
            new LinkedHashMap<String, Long>(txos.getOutputs()));
    List<Pack> reversedPacks = new LinkedList<Pack>(packs);
    Collections.reverse(reversedPacks);

    for (int i = packs.size() - 1; i >= 0; i--) { // packs in reverse order
      Pack pack = packs.get(i);
      UnpackLinkMatrixResult result = unpackLinkMatrix(matRes, newTxos, pack);
      matRes = result.getMatLnk();
      newTxos = result.getTxos();
    }
    return new UnpackLinkMatrixResult(newTxos, matRes);
  }

  /**
   * Unpacks txos in the linkability matrix for one pack.
   *
   * @param matLnk linkability matrix to be unpacked
   * @param txos packed txos containing the pack
   * @param pack pack to unpack
   * @return UnpackLinkMatrixResult
   */
  protected UnpackLinkMatrixResult unpackLinkMatrix(
      final ObjectBigList<IntBigList> matLnk, Txos txos, final Pack pack) {

    ObjectBigList<IntBigList> newMatLnk = null;
    Txos newTxos = txos;

    if (matLnk != null) {
      if (PackType.INPUTS.equals(pack.getPackType())) {
        // unpack txos
        Map<String, Long> newInputs = new LinkedHashMap<String, Long>(txos.getInputs());
        final int idx = unpackTxos(txos.getInputs(), pack, newInputs);
        newTxos = new Txos(newInputs, txos.getOutputs());

        // unpack matLnk
        int nbIns = txos.getInputs().size() + pack.getIns().size() - 1;
        int nbOuts = txos.getOutputs().size();
        final ObjectBigList<IntBigList> newMatLnkFinal =
            new ObjectBigArrayBigList<IntBigList>(nbOuts /*,nbIns*/);
        for (int i = 0; i < nbOuts; i++) {
          IntBigList line = new IntBigArrayBigList(nbIns);
          for (int j = 0; j < nbIns; j++) {
            if (j < idx) {
              // keep values before pack
              line.add(j, matLnk.get(i).getInt(j));
            } else if (j >= (idx + pack.getIns().size())) {
              // keep values after pack
              line.add(j, matLnk.get(i).getInt(j - pack.getIns().size() + 1));
            } else {
              // insert values for unpacked txos
              line.add(j, matLnk.get(i).getInt(idx));
            }
          }
          newMatLnkFinal.add(line);
        }
        newMatLnk = newMatLnkFinal;
      }
    }
    return new UnpackLinkMatrixResult(newTxos, newMatLnk);
  }

  /**
   * Unpack txos for one pack.
   *
   * @param currentTxos packed txos containing the pack
   * @param unpackedTxos map to return unpacked txos
   * @param pack pack to unpack
   * @return idx pack indice in txos
   */
  protected int unpackTxos(
      Map<String, Long> currentTxos, Pack pack, Map<String, Long> unpackedTxos) {
    // find pack indice in txos
    String[] txoKeys = currentTxos.keySet().toArray(new String[] {});
    int idx;
    for (idx = 0; idx < txoKeys.length; idx++) {
      if (pack.getLbl().equals(txoKeys[idx])) {
        break;
      }
    }

    unpackedTxos.clear();
    Iterator<Map.Entry<String, Long>> currentTxosIterator = currentTxos.entrySet().iterator();
    // keep txos before pack
    for (int i = 0; i < idx; i++) {
      Map.Entry<String, Long> entry = currentTxosIterator.next();
      unpackedTxos.put(entry.getKey(), entry.getValue());
    }
    // insert packed txos
    for (Entry<String, Long> entry : pack.getIns()) {
      unpackedTxos.put(entry.getKey(), entry.getValue());
    }
    currentTxosIterator.next(); // skip packed txo
    // keep txos after pack
    while (currentTxosIterator.hasNext()) {
      Map.Entry<String, Long> entry = currentTxosIterator.next();
      unpackedTxos.put(entry.getKey(), entry.getValue());
    }
    return idx;
  }

  /** Computes several data structures which will be used later */
  private TxosAggregates prepareData(Txos txos) {
    TxosAggregatesData allInAgg = prepareTxos(txos.getInputs());
    TxosAggregatesData allOutAgg = prepareTxos(txos.getOutputs());
    return new TxosAggregates(allInAgg, allOutAgg);
  }

  /**
   * Computes several data structures related to a list of txos
   *
   * @param initialTxos list of txos (list of tuples (id, value))
   * @return list of txos sorted by decreasing values array of aggregates (combinations of txos) in
   *     binary format array of values associated to the aggregates
   */
  private TxosAggregatesData prepareTxos(Map<String, Long> initialTxos) {
    Map<String, Long> txos = new LinkedHashMap<String, Long>();

    // Orders txos by decreasing value
    Comparator<Entry<String, Long>> comparingByValueReverse =
        Collections.reverseOrder(ListsUtils.<String, Long>comparingByValue());
    for (Entry<String, Long> entry :
        ListsUtils.sortMap(initialTxos, comparingByValueReverse).entrySet()) {
      // Removes txos with null value
      if (entry.getValue() > 0) {
        txos.put(entry.getKey(), entry.getValue());
      }
    }

    // Creates a 1D array of values
    final Long[] allVal = txos.values().toArray(new Long[] {});
    List<Long> allIndexes = new ArrayList<Long>();
    for (long i = 0; i < txos.size(); i++) {
      allIndexes.add(i);
    }

    //// int[][] allAgg = ListsUtils.powerSet(allVal);

    long nbAggregates = (long) Math.pow(2, allIndexes.size());
    if (log.isDebugEnabled()) {
      if (log.isDebugEnabled()) {
        Utils.logMemory(
            "Computing aggregates for "
                + initialTxos.size()
                + " utxos: "
                + nbAggregates
                + " aggregates...");
      }
    }
    ObjectBigList<long[]> allAggIndexes = ListsUtils.powerSet(allIndexes.toArray(new Long[] {}));

    // int[] allAggVal = Arrays.stream(allAgg).mapToInt(array ->
    // Arrays.stream(array).sum()).toArray();
    List<Long> allAggVal = new LinkedList<Long>();
    final String PROGRESS_ID = "prepareTxos";
    for (long[] array : allAggIndexes) {
      allAggVal.add(
          LongStreams.of(array)
              .map(
                  new LongUnaryOperator() {
                    @Override
                    public long applyAsLong(long indice) {
                      return allVal[(int) indice]; // TODO !!! cast
                    }
                  })
              .sum());

      Utils.logProgress(PROGRESS_ID, allAggVal.size(), nbAggregates);
    }
    Utils.logProgressDone(PROGRESS_ID, nbAggregates);
    return new TxosAggregatesData(txos, allAggIndexes, ListsUtils.toPrimitiveArray(allAggVal));
  }

  // LIMITS
  private boolean checkLimitOk(Txos txos) {
    int lenIn = txos.getInputs().size();
    int lenOut = txos.getOutputs().size();
    int maxCard = Math.max(lenIn, lenOut);
    if (maxTxos != null && maxCard > maxTxos) {
      System.out.println("maxTxos limit reached!");
      return false;
    }
    return true;
  }
}
