package ru.yandex.practicum.filmorate.model.film;

public enum FilmGenreEnum {
    COMEDY, DRAMA, CARTOON, THRILLER, DOCUMENTARY, ACTION;

    public static FilmGenreEnum from(String genre) {
        return switch (genre.toLowerCase()) {
          case "comedy" -> COMEDY;
          case "drama" -> DRAMA;
          case "cartoon" -> CARTOON;
          case "thriller" -> THRILLER;
          case "documentary" -> DOCUMENTARY;
          case "action" -> ACTION;
          default -> null;
        };
    }

    public static String fromRussia(FilmGenreEnum filmGenreEnum) {
        return switch (filmGenreEnum) {
            case COMEDY -> "Комедия";
            case DRAMA -> "Драма";
            case CARTOON -> "Мультфильм";
            case THRILLER -> "Триллер";
            case DOCUMENTARY -> "Документальный";
            case ACTION -> "Боевик";
        };
    }
}
