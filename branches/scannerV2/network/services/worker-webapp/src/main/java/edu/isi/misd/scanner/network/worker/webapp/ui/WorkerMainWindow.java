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

import org.activiti.explorer.ui.MainWindow;
import org.activiti.explorer.ui.mainlayout.ExplorerLayout;

/**
 * @author Joram Barrez
 * @author mdarcy hacked it to customize UI features
 */
public class WorkerMainWindow extends MainWindow
{
  
    @Override
    public void showDefaultContent() {
      showingLoginPage = false;
      removeStyleName(ExplorerLayout.STYLE_LOGIN_PAGE);
      addStyleName("Default style"); // Vaadin bug: must set something or old style (eg. login page style) is not overwritten

      // init general look and feel
      mainLayout = new WorkerMainLayout();
      setContent(mainLayout);

      // init hidden components
      initHiddenComponents();
    }

}
