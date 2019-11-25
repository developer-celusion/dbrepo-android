package com.mobiliteam.dbrepo.offlinesync;

/**
 * Created by swapnilnandgave on 19/04/18.
 */

public interface ISyncTrackListener {

    void pushedInIDMapping(IDMapping idMapping);

    void pushedInSync(SyncMapping syncMapping);

}
