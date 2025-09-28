package at.fhtw.mrp.dto;

public enum MediaEntryType {
    MOVIE,
    SERIES,
    GAME;

    public static MediaEntryType parse(String mediaEntryType) {
        return MediaEntryType.valueOf(mediaEntryType.toUpperCase());
    }
}
