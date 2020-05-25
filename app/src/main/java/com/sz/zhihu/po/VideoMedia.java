package com.sz.zhihu.po;

public class VideoMedia {
    private int videoId;
    private String path;
    private int duration;
    private long size;
    private String displayName;
    private long modifyTime;
    private String thumbPath;
    private String dirPath;

    public VideoMedia(String path,String thumbPath,int duration,long size,String displayName){
        this.path = path;
        this.thumbPath = thumbPath;
        this.duration = duration;
        this.size = size;
        this.displayName = displayName;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDisplayName() {
        return displayName;
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    @Override
    public String toString() {
        return "VideoMedia{" +
                "videoId=" + videoId +
                ", path='" + path + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", displayName='" + displayName + '\'' +
                ", modifyTime=" + modifyTime +
                ", thumbPath='" + thumbPath + '\'' +
                ", dirPath='" + dirPath + '\'' +
                '}';
    }
}
