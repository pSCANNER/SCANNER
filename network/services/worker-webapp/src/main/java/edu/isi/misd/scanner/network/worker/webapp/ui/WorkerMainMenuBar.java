/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.isi.misd.scanner.network.worker.webapp.ui;

import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.Messages;
import org.activiti.explorer.ViewManager;
import org.activiti.explorer.ui.Images;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.activiti.explorer.ui.mainlayout.MainMenuBar;


/**
 * @author Joram Barrez
 * @author Frederik Heremans
 * @author mdarcy hacked to customize UI features
 */
@SuppressWarnings("serial")
public class WorkerMainMenuBar extends MainMenuBar 
{

 
    @Override
    protected void initButtons() {
      Button taskButton = addMenuButton(ViewManager.MAIN_NAVIGATION_TASK, i18nManager.getMessage(Messages.MAIN_MENU_TASKS), Images.MAIN_MENU_TASKS, false, 80);
      taskButton.addListener(new ShowTasksClickListener());
      menuItemButtons.put(ViewManager.MAIN_NAVIGATION_TASK, taskButton);

      if (ExplorerApp.get().getLoggedInUser().isAdmin()) {    
          Button processButton = addMenuButton(ViewManager.MAIN_NAVIGATION_PROCESS, i18nManager.getMessage(Messages.MAIN_MENU_PROCESS), Images.MAIN_MENU_PROCESS, false, 80);
          processButton.addListener(new ShowProcessDefinitionsClickListener());
          menuItemButtons.put(ViewManager.MAIN_NAVIGATION_PROCESS, processButton);
      }
      Button reportingButton = addMenuButton(ViewManager.MAIN_NAVIGATION_REPORT, i18nManager.getMessage(Messages.MAIN_MENU_REPORTS), Images.MAIN_MENU_REPORTS, false, 80);
      reportingButton.addListener(new ShowReportsClickListener());
      menuItemButtons.put(ViewManager.MAIN_NAVIGATION_REPORT, reportingButton);

      if (ExplorerApp.get().getLoggedInUser().isAdmin()) {
        Button manageButton = addMenuButton(ViewManager.MAIN_NAVIGATION_MANAGE, i18nManager.getMessage(Messages.MAIN_MENU_MANAGEMENT), Images.MAIN_MENU_MANAGE, false, 90);
        manageButton.addListener(new ShowManagementClickListener());
        menuItemButtons.put(ViewManager.MAIN_NAVIGATION_MANAGE, manageButton);
      }
    }

  
  // Listener classes 
  // (mdarcy): these had private scope in the source file,
  // not sure why, so they had to be copied.
  private class ShowTasksClickListener implements ClickListener {
    public void buttonClick(ClickEvent event) {
      ExplorerApp.get().getViewManager().showInboxPage();
    }
  }
  
  private class ShowProcessDefinitionsClickListener implements ClickListener {
    public void buttonClick(ClickEvent event) {
      ExplorerApp.get().getViewManager().showDeployedProcessDefinitionPage();
    }
  }
  
  private class ShowReportsClickListener implements ClickListener {
    public void buttonClick(ClickEvent event) {
      ExplorerApp.get().getViewManager().showRunReportPage();
    }
  }
  
  private class ShowManagementClickListener implements ClickListener {
    public void buttonClick(ClickEvent event) {
      ExplorerApp.get().getViewManager().showDatabasePage();
    }
  } 
  
}
