package com.ssg.usms.business.video.constant;

import static com.ssg.usms.business.store.constant.StoreConstants.UUID_REGEX;

public class VideoConstants {

    public static final String REPLAY_VIDEO_FILE_NAME_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}-\\d{10}\\.[a-zA-Z0-9]+";
    public static final String STREAM_KEY_PATTERN = UUID_REGEX;
    public static final String LIVE_VIDEO_FILE_NAME_PATTERN = "[a-zA-Z0-9-_]+\\.[a-zA-Z0-9]+";

    public static final String SET_NAME_OF_CONNECTED_STREAM_KEY = "CONNECTED_STREAM_KEY";

}
