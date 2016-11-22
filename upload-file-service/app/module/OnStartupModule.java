package module;

import com.google.inject.AbstractModule;

import controllers.MailConsumer;

class OnStartupModule extends AbstractModule {
    @Override
    public void configure() {
        bind(MailConsumer.class).asEagerSingleton();
    }
}