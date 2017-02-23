package testingPackage;

import java.util.regex.Pattern;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

@RunWith(Parameterized.class)
public class GoogleTest {
  private WebDriver driver;
  private String baseUrl;
  private StringBuffer verificationErrors = new StringBuffer();
  public String inputText;
  public String expectedResult;
	
  public GoogleTest(String input, String expected){
	  inputText = input;
	  expectedResult = expected;
  }

  @Parameters
  public static Object[][] data() throws IOException {	  
	  FileInputStream file = new FileInputStream(new File("resource/GoogleTests.xlsx"));
	  XSSFWorkbook workbook = new XSSFWorkbook(file);
	  XSSFSheet sheet = workbook.getSheetAt(0);
	  int numRows = sheet.getLastRowNum() + 1;
	  String[][] excelInfo = new String[numRows][2];
	  Iterator<Row> rowIterator = sheet.iterator();
	  int i = 0;
	  while (rowIterator.hasNext()) {
		  Row row = rowIterator.next();
		  excelInfo[i][0] = row.getCell(0).getStringCellValue();
		  excelInfo[i][1] = row.getCell(1).getStringCellValue();
		  i++;
	  }
	  workbook.close();
	  return (Object[][]) excelInfo;
  }
  
 
  @Before
  public void setUp() throws Exception {
	//Firefox driver 
	System.setProperty("webdriver.gecko.driver", "resource/geckodriver.exe");
	//Chrome driver
	System.setProperty("webdriver.chrome.driver", "resource/chromedriver.exe");
	//I am using Chrome
	driver = new ChromeDriver();
	baseUrl = "http://www.google.com";
	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    
  }

  @Test
  public void maverickTest() throws Exception {
	driver.get(baseUrl + "/");
    driver.findElement(By.id("lst-ib")).clear();
    driver.findElement(By.id("lst-ib")).sendKeys(inputText+ Keys.RETURN);
    for (int second = 0;; second++) {
    	if (second >= 60) fail("timeout");
    	try { if (isElementPresent(By.linkText(expectedResult))) break; } catch (Exception e) {}
    	Thread.sleep(1000);
    }

    assertTrue(isElementPresent(By.linkText(expectedResult)));
    
  }

  @After
  public void tearDown() throws Exception {
    driver.close();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }
  
  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }
}
