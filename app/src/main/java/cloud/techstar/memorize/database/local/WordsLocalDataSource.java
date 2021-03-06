package cloud.techstar.memorize.database.local;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import java.util.List;

import cloud.techstar.memorize.database.Words;
import cloud.techstar.memorize.database.WordsDataSource;
import cloud.techstar.memorize.utils.AppExecutors;

public class WordsLocalDataSource implements WordsDataSource {

    private static volatile WordsLocalDataSource INSTANCE;

    private WordsDao wordsDao;

    private AppExecutors appExecutors;

    private WordsLocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull WordsDao wordsDao) {
        this.appExecutors = appExecutors;
        this.wordsDao = wordsDao;
    }

    public static WordsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull WordsDao wordsDao) {
        if (INSTANCE == null) {
            synchronized (WordsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WordsLocalDataSource(appExecutors, wordsDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getWords(@NonNull final LoadWordsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Words> words = wordsDao.getWords();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (words.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onWordsLoaded(words);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getWord(@NonNull final String wordId, @NonNull final GetWordCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Words word = wordsDao.getWordById(wordId);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (word != null) {
                            callback.onWordLoaded(word);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveWord(@NonNull final Words word) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                wordsDao.insertWord(word);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void memorizeWord(@NonNull final Words word) {
        Runnable completeRunnable = new Runnable() {
            @Override
            public void run() {
                wordsDao.updateMemorized(word.getId(), true, 2);
            }
        };

        appExecutors.diskIO().execute(completeRunnable);
    }

    @Override
    public void memorizeWord(@NonNull String wordId) {
        // Not required because the {@link WordsRepository}
    }

    @Override
    public void favWord(@NonNull final Words word) {
        Runnable completeRunnable = new Runnable() {
            @Override
            public void run() {
                wordsDao.updateFavorited(word.getId(), true, 2);
            }
        };

        appExecutors.diskIO().execute(completeRunnable);
    }

    @Override
    public void favWord(@NonNull String wordId) {
        // Not required because the {@link WordsRepository}
    }

    @Override
    public void activeWord(@NonNull final Words word) {
        Runnable completeRunnable = new Runnable() {
            @Override
            public void run() {
                wordsDao.updateMemorized(word.getId(), false, 2);
                wordsDao.updateFavorited(word.getId(), false, 2);
            }
        };

        appExecutors.diskIO().execute(completeRunnable);
    }

    @Override
    public void activeWord(@NonNull String wordId) {
        // Not required because the {@link WordsRepository}
    }

    @Override
    public void refreshWords() {
        // Not required because the {@link WordsRepository}
    }

    @Override
    public void deleteWord(@NonNull final String wordId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                wordsDao.deleteWordById(wordId);
            }
        };

        appExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteAllWords() {
        Runnable deleteAllRunnabe = new Runnable() {
            @Override
            public void run() {
                wordsDao.deleteWords();
            }
        };
        appExecutors.diskIO().execute(deleteAllRunnabe);
    }
}
