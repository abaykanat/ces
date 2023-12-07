package com.company.ces_productive.screen.dashboard;

import io.jmix.ui.model.KeyValueCollectionLoader;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("CES_DashboardScreen")
@UiDescriptor("dashboard-screen.xml")
public class DashboardScreen extends Screen {
    @Autowired
    private KeyValueCollectionLoader studDirectionDl;

    @Subscribe
    public void onInit(final InitEvent event) {
        studDirectionDl.load();
    }
}