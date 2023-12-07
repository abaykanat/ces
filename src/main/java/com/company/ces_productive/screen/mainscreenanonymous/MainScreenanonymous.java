package com.company.ces_productive.screen.mainscreenanonymous;

import io.jmix.ui.Screens;
import io.jmix.ui.UiProperties;
import io.jmix.ui.component.AppWorkArea;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.Window;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("CES_MainScreenAnonymous")
@UiDescriptor("main-screenAnonymous.xml")
@Route("anonymous")
public class MainScreenanonymous extends Screen implements Window.HasWorkArea {
    @Autowired
    private AppWorkArea workArea;
    @Autowired
    private Screens screens;
    @Autowired
    private UiProperties uiProperties;
    @Override
    public AppWorkArea getWorkArea() {
        return workArea;
    }

    @Subscribe("goLoginScreen")
    public void onGoLoginScreenClick(Button.ClickEvent event) {
        String loginScreenId = uiProperties.getLoginScreenId();
        Screen loginScreen = screens.create(loginScreenId, OpenMode.ROOT);
        loginScreen.show();
    }
}