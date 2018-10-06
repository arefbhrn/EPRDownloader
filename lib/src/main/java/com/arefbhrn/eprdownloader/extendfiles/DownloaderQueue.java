package com.arefbhrn.eprdownloader.extendfiles;

import com.arefbhrn.eprdownloader.request.DownloadRequest;

import java.util.ArrayList;

/**
 * Written by "Aref Bahreini Nejad"
 * Version: 1.0.0
 * Date: 06/10/2018
 */

public class DownloaderQueue {

    private static final ArrayList<DownloadRequest> waitingList = new ArrayList<>();
    private static final ArrayList<DownloadRequest> runningList = new ArrayList<>();
    private static int runningLimit = 3;

    /**
     * Sets concurrent download limit. Default is 3.
     *
     * @param n number of maximum concurrent downloads
     */
    public static void setRunningLimit(int n) {
        runningLimit = n;
    }

    /**
     * Adds a {@link DownloadRequest} instance to the queue.
     *
     * @param downloadRequest download request instance
     */
    public static void add(DownloadRequest downloadRequest) {
        for (DownloadRequest download : waitingList)
            if (download.getDownloadId() == downloadRequest.getDownloadId())
                return;
        for (DownloadRequest download : runningList)
            if (download.getDownloadId() == downloadRequest.getDownloadId())
                return;
        waitingList.add(downloadRequest);
        startNext();
    }

    /**
     * Gets {@link DownloadRequest} from queue
     *
     * @param downloadID download request unique id
     * @return * download request instance if available in queue or downloading
     */
    public static DownloadRequest get(int downloadID) {
        for (DownloadRequest download : waitingList)
            if (download.getDownloadId() == downloadID)
                return download;
        for (DownloadRequest download : runningList)
            if (download.getDownloadId() == downloadID)
                return download;
        return null;
    }

    /**
     * Removes {@link DownloadRequest} from the queue
     *
     * @param downloadID download request unique id
     */
    public static void remove(int downloadID) {
        for (int i = 0; i < waitingList.size(); i++) {
            if (waitingList.get(i).getDownloadId() == downloadID) {
                waitingList.remove(i);
                break;
            }
        }
        for (int i = 0; i < runningList.size(); i++) {
            if (runningList.get(i).getDownloadId() == downloadID) {
                runningList.remove(i);
                break;
            }
        }
        startNext();
    }

    /**
     * Stars next {@link DownloadRequest} in queue if is available and allowed
     */
    private static void startNext() {
        while (runningList.size() < runningLimit && waitingList.size() > 0) {
            waitingList.get(0).start();
            runningList.add(waitingList.get(0));
            waitingList.remove(0);
        }
    }

}
