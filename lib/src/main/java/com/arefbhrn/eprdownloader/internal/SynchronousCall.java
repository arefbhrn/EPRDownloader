package com.arefbhrn.eprdownloader.internal;

import com.arefbhrn.eprdownloader.Response;
import com.arefbhrn.eprdownloader.request.DownloadRequest;

public class SynchronousCall {

    public final DownloadRequest request;

    public SynchronousCall(DownloadRequest request) {
        this.request = request;
    }

    public Response execute() {
        DownloadTask downloadTask = DownloadTask.create(request);
        return downloadTask.run();
    }

}
