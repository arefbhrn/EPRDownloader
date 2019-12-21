/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.arefbhrn.eprdownloader.request;

import com.arefbhrn.eprdownloader.Error;
import com.arefbhrn.eprdownloader.OnCancelListener;
import com.arefbhrn.eprdownloader.OnDownloadListener;
import com.arefbhrn.eprdownloader.OnPauseListener;
import com.arefbhrn.eprdownloader.OnProgressListener;
import com.arefbhrn.eprdownloader.OnStartOrResumeListener;
import com.arefbhrn.eprdownloader.Priority;
import com.arefbhrn.eprdownloader.Response;
import com.arefbhrn.eprdownloader.Status;
import com.arefbhrn.eprdownloader.core.Core;
import com.arefbhrn.eprdownloader.internal.ComponentHolder;
import com.arefbhrn.eprdownloader.internal.DownloadRequestQueue;
import com.arefbhrn.eprdownloader.internal.SynchronousCall;
import com.arefbhrn.eprdownloader.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by amitshekhar on 13/11/17.
 * Updated by "Aref Bahreini Nejad" on 21/12/2019
 */

public class DownloadRequest {

    private Priority priority;
    private Object tag;
    private String url;
    private String dirPath;
    private String fileName;
    private int sequenceNumber;
    private Future future;
    private long downloadedBytes;
    private long totalBytes;
    private int readTimeout;
    private int connectTimeout;
    private String userAgent;
    private ArrayList<OnProgressListener> onProgressListeners = new ArrayList<>();
    private ArrayList<OnDownloadListener> onDownloadListeners = new ArrayList<>();
    private ArrayList<OnStartOrResumeListener> onStartOrResumeListeners = new ArrayList<>();
    private ArrayList<OnPauseListener> onPauseListeners = new ArrayList<>();
    private ArrayList<OnCancelListener> onCancelListeners = new ArrayList<>();
    private int downloadId = -1;
    private HashMap<String, List<String>> headerMap;
    private Status status;

    DownloadRequest(DownloadRequestBuilder builder) {
        this.url = builder.url;
        this.dirPath = builder.dirPath;
        this.fileName = builder.fileName;
        this.headerMap = builder.headerMap;
        this.priority = builder.priority;
        this.tag = builder.tag;
        this.readTimeout =
                builder.readTimeout != 0 ?
                        builder.readTimeout :
                        getReadTimeoutFromConfig();
        this.connectTimeout =
                builder.connectTimeout != 0 ?
                        builder.connectTimeout :
                        getConnectTimeoutFromConfig();
        this.userAgent = builder.userAgent;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public HashMap<String, List<String>> getHeaders() {
        return headerMap;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getUserAgent() {
        if (userAgent == null) {
            userAgent = ComponentHolder.getInstance().getUserAgent();
        }
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getDownloadId() {
        if (downloadId == -1)
            downloadId = Utils.getUniqueId(url, dirPath, fileName);
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ArrayList<OnProgressListener> getOnProgressListeners() {
        return onProgressListeners;
    }

    public DownloadRequest addOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListeners.add(onDownloadListener);
        return this;
    }

    public DownloadRequest addOnStartOrResumeListener(OnStartOrResumeListener onStartOrResumeListeners) {
        this.onStartOrResumeListeners.add(onStartOrResumeListeners);
        return this;
    }

    public DownloadRequest addOnProgressListener(OnProgressListener onProgressListeners) {
        this.onProgressListeners.add(onProgressListeners);
        return this;
    }

    public DownloadRequest addOnPauseListener(OnPauseListener onPauseListeners) {
        this.onPauseListeners.add(onPauseListeners);
        return this;
    }

    public DownloadRequest addOnCancelListener(OnCancelListener onCancelListeners) {
        this.onCancelListeners.add(onCancelListeners);
        return this;
    }

    public int start() {
        DownloadRequestQueue.getInstance().addRequest(this);
        return getDownloadId();
    }

    public Response executeSync() {
        return new SynchronousCall(this).execute();
    }

    public void deliverError(final Error error) {
        if (status != Status.CANCELLED) {
            setStatus(Status.FAILED);
            for (final OnDownloadListener onDownloadListener : onDownloadListeners) {
                Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                        .execute(new Runnable() {
                            public void run() {
                                if (onDownloadListener != null) {
                                    onDownloadListener.onError(error);
                                }
                            }
                        });
            }
            finish();
        }
    }

    public void deliverSuccess() {
        if (status != Status.CANCELLED) {
            setStatus(Status.COMPLETED);
            for (final OnDownloadListener onDownloadListener : onDownloadListeners) {
                Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                        .execute(new Runnable() {
                            public void run() {
                                if (onDownloadListener != null) {
                                    onDownloadListener.onDownloadComplete();
                                }
                            }
                        });
            }
            finish();
        }
    }

    public void deliverStartEvent() {
        if (status != Status.CANCELLED) {
            for (final OnStartOrResumeListener onStartOrResumeListener : onStartOrResumeListeners) {
                Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                        .execute(new Runnable() {
                            public void run() {
                                if (onStartOrResumeListener != null) {
                                    onStartOrResumeListener.onStartOrResume();
                                }
                            }
                        });
            }
        }
    }

    public void deliverPauseEvent() {
        if (status != Status.CANCELLED) {
            for (final OnPauseListener onPauseListener : onPauseListeners) {
                Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                        .execute(new Runnable() {
                            public void run() {
                                if (onPauseListener != null) {
                                    onPauseListener.onPause();
                                }
                            }
                        });
            }
        }
    }

    private void deliverCancelEvent() {
        for (final OnCancelListener onCancelListener : onCancelListeners) {
            Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(new Runnable() {
                        public void run() {
                            if (onCancelListener != null) {
                                onCancelListener.onCancel();
                            }
                        }
                    });
        }
    }

    public void cancel() {
        status = Status.CANCELLED;
        if (future != null) {
            future.cancel(true);
        }
        deliverCancelEvent();
        Utils.deleteTempFileAndDatabaseEntryInBackground(Utils.getTempPath(dirPath, fileName), getDownloadId());
    }

    private void finish() {
        destroy();
        DownloadRequestQueue.getInstance().finish(this);
    }

    private void destroy() {
        this.onProgressListeners.clear();
        this.onDownloadListeners.clear();
        this.onStartOrResumeListeners.clear();
        this.onPauseListeners.clear();
        this.onCancelListeners.clear();
    }

    private int getReadTimeoutFromConfig() {
        return ComponentHolder.getInstance().getReadTimeout();
    }

    private int getConnectTimeoutFromConfig() {
        return ComponentHolder.getInstance().getConnectTimeout();
    }

}
