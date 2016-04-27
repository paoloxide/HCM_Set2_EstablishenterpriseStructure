package hcm.tests;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;
import static util.ReportLogger.log;
import static util.ReportLogger.logFailure;

public class ExcelFileValidation extends BaseTest{
	
	private ArrayList<String> visibleTasksHolder = new ArrayList<String>();
	private List<String> validEntries = new ArrayList<String>();
	private String mainTaskPath = "";
	private boolean isScrollingDown = true;
	private static final int MAX_TIME_OUT = 60;
	
	@Test
	public void a_test() throws Exception  {
		
		testReportFormat();
	
	try{
		validateExcelFile();
	  
	  	}
	
        catch (AssertionError ae)
        {
            //takeScreenshot();
            logFailure(ae.getMessage());

            throw ae;
        }
        catch (Exception e)
        {
            //takeScreenshot();
            logFailure(e.getMessage());

            throw e;
        }
    }

	public void validateExcelFile() throws Exception{
			
		LoginPage login = new LoginPage(driver);
		//takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		//takeScreenshot();
		welcome.clickNavigator("More...");
		clickNavigationLink("Setup and Maintenance");
			
		TaskListManagerTopPage task = new TaskListManagerTopPage(driver);
		//takeScreenshot();
		
		task.clickTask("Configure Offerings");
		Thread.sleep(1000);
		//takeScreenshot();
			
		//Enable task
		String[] taskStatusHolder = {};
		boolean hasNextExcelEntry = true;
		String scanType = getExcelData(2, BaseTest.scanType, "text");
		//NOTE: Use afrrk to identify a mainTask, succeeding subfolder use as afrap; //div[text()='Compensation Management']
		TaskUtilities.customWaitForElementVisibility("//a/img[contains(@title,'Feature')]", MAX_TIME_OUT);
		
		if(scanType.contentEquals("FULL")){
				fullScan(task);
			} else if(scanType.contentEquals("SMART")){
				smartScan(task);
			}
		
		//Find all possible status for each tasks
		if(is_element_visible(mainTaskPath+"/../../td/span/a", "xpath")){
			task.scrollElementIntoView(mainTaskPath);
			//task.clickTaskStatus(mainTaskPath);
			TaskUtilities.jsFindThenClick(mainTaskPath+"/../../td/span/a");
			task.waitForElementToBeClickable(8, "//div/span/input");
			taskStatusHolder = task.queryAlltaskstatusFolder();
			Thread.sleep(250);
		}
		
		//Finally Adding all status entries...
		for(int m=0;m<taskStatusHolder.length;m++){
			validEntries.add(taskStatusHolder[m]);
		}
		
		System.out.println("All valid entries: "+validEntries+"\nSize: "+validEntries.size());

		for(int rowNum =2; getExcelData(rowNum, defaultmainTaskValue).length() > 0; rowNum++ ){
				excelEntryloop:
				for(int colNum = defaultmainTaskValue; (hasNextExcelEntry) ; colNum++){
					
					String excelEntry = (String)getExcelData(rowNum, colNum);
					System.out.println("Entry No: \nFilled excelEntry...."+excelEntry);

					if(getExcelData(rowNum, colNum).length() <= 0 && getExcelData(rowNum, colNum+1).length() > 0 ){
						log(excelEntry+" is an invalid entry!!!\n;");
						System.out.println(excelEntry+" is an invalid entry!!!\n");
						throw new IllegalArgumentException();
					}else if(getExcelData(rowNum, colNum).length() <= 0){
						 break excelEntryloop;
					}

					arrayEntryloop:
					for(int arrayInt = 0; arrayInt < validEntries.size()+2; arrayInt++){
						String arrayEntry = (String)validEntries.get(arrayInt);
						System.out.println("Filled arrayEntry...."+arrayEntry);
						System.out.println(arrayEntry+" vs. "+excelEntry);
						if(arrayEntry.equals(excelEntry) && !excelEntry.equals("")){
							System.out.println(excelEntry+" is found to be valid...");
							break arrayEntryloop;
						}

						System.out.println(arrayInt+" vs. "+validEntries.size());
						if((int)arrayInt+1 >= (int)validEntries.size()){
							log(excelEntry+" is an invalid entry!!!\n;");
							System.out.println(excelEntry+" is an invalid entry!!!\n");
							throw new IllegalArgumentException();
						}
					}
				}
		}
		
		//Ending message::
				
				System.out.println("Validation Completed\n***************\n");
				log("Validation Completed");
				
				Thread.sleep(1000);
				//takeScreenshot();
	}
	
	public void fullScan(TaskListManagerTopPage task) throws Exception{
		
		while(isScrollingDown){
			visibleTasksHolder = task.queryAllVisibleTasks(visibleTasksHolder);
			if(visibleTasksHolder != null){
				
				for(String visTask: visibleTasksHolder){
					validEntries.add(visTask);
				}
				mainTaskPath = visibleTasksHolder.get(0);
			}
			isScrollingDown = task.jsScrollDown(isScrollingDown);
		}
	}

	public void smartScan(final TaskListManagerTopPage task) throws Exception{
		scanloop:	
		for(int rowNum = 2; getExcelData(rowNum, defaultmainTaskValue, "text").length()>0; rowNum++){
			mainTaskPath = "//div[text()='"+getExcelData(rowNum, defaultmainTaskValue, "text")+"']" ;
			TaskUtilities.customWaitForElementVisibility(mainTaskPath, MAX_TIME_OUT, new CustomRunnable() {
				
				@Override
				public void customRun() throws Exception {
					// TODO Auto-generated method stub
					isScrollingDown = task.jsScrollDown(isScrollingDown);
				}
			});
			TaskUtilities.retryingFindClick(By.xpath(mainTaskPath));
			TaskUtilities.jsFindThenClick(mainTaskPath);
			if(!is_element_visible(mainTaskPath+"/span/a[@title='Collapse']", "xpath")){
				validEntries.add(getExcelData(rowNum, defaultmainTaskValue, "text"));
				continue scanloop;
			}
			TaskUtilities.retryingFindClick(By.xpath("//img[@title='Show as Top']/..[@href='#']"));
			TaskUtilities.retryingFindClick(By.xpath(mainTaskPath));
			TaskUtilities.customWaitForElementVisibility("//a[@title='Show Hierarchy']", MAX_TIME_OUT);
			
			visibleTasksHolder = task.queryAllVisibleTasks(visibleTasksHolder);
				
				for(String visTask: visibleTasksHolder){
					validEntries.add(visTask);
				}
			
			TaskUtilities.customWaitForElementVisibility("//img[@title='Go Up']/..[@href='#']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//img[@title='Go Up']/..[@href='#']");
			TaskUtilities.customWaitForElementEnablement(By.xpath("//img[@title='Show as Top']/..[@href='#']/.."), MAX_TIME_OUT);
		}
	}
}
