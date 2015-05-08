package joption;

import org.junit.Before;
import org.junit.Test;
import rx.functions.Func0;
import rx.functions.Func1;

import java.util.HashMap;
import java.util.Map;

import static joption.Option.option;
import static org.junit.Assert.assertEquals;

public class OptionTest {

    private Map<Integer, String> team;
    private Func1<Integer, String> lookupMember;

    @Before
    public void beforeEachTestCase() {
        populateTeam();
        createLookupMember();
    }

    private void createLookupMember() {
        lookupMember = new Func1<Integer, String>() {
            @Override
            public String call(Integer i) {
                return team.get(i);
            }
        };
    }

    private void populateTeam() {
        team = new HashMap<>(3);
        team.put(1, "Istvan");
        team.put(2, "Maria");
        team.put(3, "Timea");
    }

    @Test
    public void testMapChain() {
        Option.option(1).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                return "one";
            }
        }).map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                return 2;
            }
        }).flatMap(new Func1<Integer, Option<String>>() {
            @Override
            public Option<String> call(Integer integer) {
                return option("two");
            }
        }).orElse(new Func0<Option<String>>() {
            @Override
            public Option<String> call() {
                return null;
            }
        }).getOrElse("unknown");
    }

    @Test
    public void testMapSome() {
        assertEquals(team.get(1), option(1).map(lookupMember).getOrElse("unknown"));
    }

    @Test
    public void testMapNone() {
        String unknown = "unknown";
        int falseId = -1;
        assertEquals(unknown, option(falseId).map(lookupMember).getOrElse(unknown));
    }

    @Test(expected = ExpectedException.class)
    public void testMapException() {
        final Func1<Integer, String> failingLookup = new Func1<Integer, String>() {
            public String call(Integer i) {
                throw new ExpectedException();
            }
        };
        option(1).map(failingLookup);
    }

    @Test
    public void testFlatMapSome() {
        final Func1<Integer, Option<String>> memberOption = new Func1<Integer, Option<String>>() {
            @Override
            public Option<String> call(Integer i) {
                return option(team.get(i));
            }
        };
        assertEquals(team.get(1), option(1).flatMap(memberOption).getOrElse("unknown"));
    }

    @Test
    public void testFlatMapNone() {
        String unknown = "unknown";
        int falseId = -1;
        final Func1<Integer, Option<String>> memberOption = new Func1<Integer, Option<String>>() {
            @Override
            public Option<String> call(Integer i) {
                return option(team.get(i));
            }
        };
        assertEquals(unknown, option(falseId).flatMap(memberOption).getOrElse(unknown));
    }

    @Test
    public void testFlatMapException() {
        String unknown = "unknown";
        int falseId = -1;
        final Func1<Integer, Option<String>> memberOption = new Func1<Integer, Option<String>>() {
            @Override
            public Option<String> call(Integer i) {
                throw new IllegalArgumentException("Illegal member ID: " + i);
            }
        };
        assertEquals(unknown, option(falseId).flatMap(memberOption).getOrElse(unknown));
    }

    @Test
    public void testOrElseSome() {
        final String unknown = "unknown";
        final int falseId = -1;
        final int validId = 1;
        final Func1<Integer, Option<String>> memberOption = new Func1<Integer, Option<String>>() {
            @Override
            public Option<String> call(Integer i) {
                return option(team.get(i));
            }
        };
        final Func0<Option<String>> fallback = new Func0<Option<String>>() {
            @Override
            public Option<String> call() {
                return option(team.get(validId));
            }
        };
        assertEquals(team.get(validId), option(falseId).flatMap(memberOption).orElse(fallback).getOrElse(unknown));
    }

    @Test
    public void testOrElseNone() {
        final String unknown = "unknown";
        final int falseId = -1;
        final Func1<Integer, Option<String>> memberOption = new Func1<Integer, Option<String>>() {
            @Override
            public Option<String> call(Integer i) {
                return option(team.get(i));
            }
        };
        final Func0<Option<String>> fallback = new Func0<Option<String>>() {
            @Override
            public Option<String> call() {
                return Option.none();
            }
        };
        assertEquals(unknown, option(falseId).flatMap(memberOption).orElse(fallback).getOrElse(unknown));
    }

    public static class ExpectedException extends RuntimeException {
    }

}