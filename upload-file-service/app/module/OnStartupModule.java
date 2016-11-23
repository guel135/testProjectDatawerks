package module;

import com.google.inject.AbstractModule;

import controllers.MessageConsumerController;

class OnStartupModule extends AbstractModule {
    @Override
    public void configure() {
        bind(MessageConsumerController.class).asEagerSingleton();
    }
}