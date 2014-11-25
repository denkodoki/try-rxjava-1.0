package resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

public class ResourceHandlingTest {

    final static Logger LOGGER = LoggerFactory.getLogger(ResourceHandlingTest.class);

    @Test
    public void watchDirChanges() throws IOException, InterruptedException {
        final Path dirToWatch = createDirToWatch();
        final WatchService watchService = createWatchServiceFor(dirToWatch);
        final WatchEvent.Kind[] watchEventKinds = createWatchEventKinds();
        final long pollingInterval = 500;
        final TimeUnit pollingTimeUnit = TimeUnit.MILLISECONDS;
        final Observable<WatchEvent> watchEventStream = WatchEventObservable.from(
                watchService, dirToWatch, pollingInterval, pollingTimeUnit, watchEventKinds);
        final Subscription subscription = watchEventStream.doOnNext(createPintEventAction()).subscribe();
        final Path fileA = dirToWatch.resolve("tmp-file-A");
        final Path fileB = dirToWatch.resolve("tmp-file-B");
        write(fileA, "line1", "line2");
        write(fileB, "line1", "line2", "line3");
        write(fileA, "line3", "line4", "line5");
        Thread.sleep(1000);
        subscription.unsubscribe();
    }

    private Path createDirToWatch() {
        final Path dirToWatch = Paths.get("target", "tmp-unit-test").toAbsolutePath();
        dirToWatch.toFile().mkdirs();
        LOGGER.debug("Dir to watch: {}", dirToWatch);
        return dirToWatch;
    }

    private WatchService createWatchServiceFor(Path path) throws IOException {
        return path.getFileSystem().newWatchService();
    }

    private WatchEvent.Kind[] createWatchEventKinds() {
        return new WatchEvent.Kind[] {
                ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE, OVERFLOW
        };
    }

    private Action1<WatchEvent> createPintEventAction() {
        return new Action1<WatchEvent>() {
            @Override
            public void call(WatchEvent watchEvent) {
                final WatchEvent.Kind eventKind = watchEvent.kind();
                final String path = watchEvent.context() instanceof Path? watchEvent.context().toString(): "";
                LOGGER.debug("{} {}", eventKind, path);
            }
        };
    }

    private void write(Path file, String... lines) throws IOException {
        OpenOption openOption = Files.exists(file)? StandardOpenOption.APPEND : StandardOpenOption.CREATE;
        Files.write(file, Arrays.asList(lines), Charset.defaultCharset(), openOption);
    }

}
