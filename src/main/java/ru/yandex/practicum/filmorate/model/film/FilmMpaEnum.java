package ru.yandex.practicum.filmorate.model.film;

public enum FilmMpaEnum {
    G, PG, PG_13, R, NC_17;

    public static FilmMpaEnum from(String rating) {
        if (rating != null) {
            return switch (rating.toLowerCase()) {
                case "g" -> G;
                case "pg" -> PG;
                case "pg_13" -> PG_13;
                case "r" -> R;
                case "nc_17" -> NC_17;
                default -> null;
            };
        } else {
            return null;
        }
    }

    public static String mapToString(FilmMpaEnum filmMpaEnum) {
        return switch (filmMpaEnum) {
            case G -> "G";
            case PG -> "PG";
            case PG_13 -> "PG-13";
            case R -> "R";
            case NC_17 -> "NC-17";
        };
    }
}