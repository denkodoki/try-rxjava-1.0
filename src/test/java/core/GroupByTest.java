package core;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class GroupByTest {

    private static enum ModuleType {A, B, Unknown}

    private static abstract class Message { public String toString() {return this.getClass().getSimpleName();}}
    private static class MessageA extends Message { }
    private static class MessageB extends Message { }

    private final static MessageA MESSAGE_A = new MessageA();
    private final static MessageB MESSAGE_B = new MessageB();
    private final static Message  UNKNOWN   = new Message() { };

    @Test
    public void modellingModuleBasedDispatch() {
        PublishSubject<Message> sourceSubject = PublishSubject.create();

        Observable<GroupedObservable<ModuleType, Message>> messageStreams = sourceSubject.groupBy(moduleTypeSelector);
        messageStreams.subscribe(new Dispatcher(moduleFactory));

        sourceSubject.onNext(MESSAGE_A);
        sourceSubject.onNext(MESSAGE_A);
        sourceSubject.onNext(MESSAGE_B);
        sourceSubject.onNext(UNKNOWN);
        sourceSubject.onNext(MESSAGE_A);
        sourceSubject.onNext(MESSAGE_B);
        sourceSubject.onNext(UNKNOWN);
        sourceSubject.onNext(MESSAGE_B);

        sourceSubject.onCompleted();
        messageStreams.toBlocking().lastOrDefault(null);
    }

    private final static Func1<Message, ModuleType> moduleTypeSelector = new Func1<Message, ModuleType>() {
        @Override
            public ModuleType call(Message m) {
            if (MessageA.class.equals(m.getClass())) {
                return ModuleType.A;
            } else if (MessageB.class.equals(m.getClass())) {
                return ModuleType.B;
            } else {
                return ModuleType.Unknown;
            }
        }
    };

    private final static Func1<ModuleType, Module> moduleFactory = new Func1<ModuleType, Module>() {
        @Override
        public Module call(ModuleType moduleType) {
            if (ModuleType.A == moduleType) {
                return new ModuleA();
            } else if (ModuleType.B == moduleType) {
                return new ModuleB();
            } else {
                return new UnknownMessageModule();
            }
        }
    };

    private static class Dispatcher implements Observer<GroupedObservable<ModuleType, Message>> {
        private final Func1<ModuleType, Module> moduleFactory;

        private Dispatcher(Func1<ModuleType, Module> moduleFactory) {
            this.moduleFactory = moduleFactory;
        }

        @Override
        public void onCompleted() {
            System.out.println("Stream of streams completed");
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("Error on stream of streams: " + throwable);
            throwable.printStackTrace();
        }

        @Override
        public void onNext(GroupedObservable<ModuleType, Message> msgStream) {
            Module module = moduleFactory.call(msgStream.getKey());
            module.subscribeTo(msgStream.observeOn(Schedulers.io()));
        }
    }

    private static abstract class Module {
        private final ModuleType moduleType;
        private final Observer<Message> messageObserver = createMessageObserver();

        private Module(ModuleType moduleType) {
            this.moduleType = moduleType;
        }

        public abstract void subscribeTo(Observable<Message> msgStream);

        protected Observer<Message> getMessageObserver() {
            return messageObserver;
        }

        private Observer<Message> createMessageObserver() {
            return new Observer<Message>() {
                @Override
                public void onCompleted() {
                    print(moduleType + " stream completed");
                }

                @Override
                public void onError(Throwable throwable) {
                    print("Error on " + moduleType + "stream: " + throwable);
                }

                @Override
                public void onNext(Message m) {
                    print("Module" + moduleType + "; message: " + m);
                }

                private void print(String msg) {
                    System.out.println(Thread.currentThread().getName() + "; " + msg);
                }
            };
        }
    }

    private static class ModuleA extends Module {

        public ModuleA() {
            super(ModuleType.A);
        }

        @Override
        public void subscribeTo(Observable<Message> msgStream) {
            msgStream.subscribe(getMessageObserver());
        }
    }

    private static class ModuleB extends Module {

        public ModuleB() {
            super(ModuleType.B);
        }

        @Override
        public void subscribeTo(Observable<Message> msgStream) {
            msgStream.subscribe(getMessageObserver());
        }
    }

    private static class UnknownMessageModule extends Module {

        public UnknownMessageModule() {
            super(ModuleType.Unknown);
        }

        @Override
        public void subscribeTo(Observable<Message> msgStream) {
            msgStream.take(0).subscribe(getMessageObserver());
        }
    }
}
