package com.wrmsr.stell.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import junit.framework.TestCase;

public class GuiceTest
        extends TestCase {
    public interface Fooer {
        void foo();
    }

    public static class FooerImpl implements Fooer {
        public void foo() {
            System.out.println(this);
        }
    }

    public static class FooUser {
        @Inject
        public FooUser(Fooer fooer) {
            this.fooer = fooer;
        }

        private final Fooer fooer;

        public void useFoo() {
            fooer.foo();
        }
    }

    public static class FooModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Fooer.class).to(FooerImpl.class);
            bind(FooUser.class);
        }
    }

    public void testInject() throws Throwable {
        Injector inj = Guice.createInjector(new FooModule());
        inj.getInstance(FooUser.class).useFoo();
    }
}
