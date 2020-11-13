package cycling.stats.scraping;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.util.Scanner;

public class CyclingStatsScraping {

    public static void main(String[] args) {

        try {

            // take user input
            Scanner Scan = new Scanner(System.in);
            System.out.println("Enter cyclist name: ");
            String nameOfCyclist = Scan.nextLine();

            // initiate web client
            WebClient client = new WebClient(BrowserVersion.BEST_SUPPORTED);

            // web client options
            client.getOptions().setJavaScriptEnabled(false);
            client.getOptions().setCssEnabled(true);
            client.getOptions().setThrowExceptionOnScriptError(false);
            client.getOptions().setThrowExceptionOnFailingStatusCode(false);

            // start page
            HtmlPage page = client.getPage("https://www.procyclingstats.com/search.php");

            // find search input field, focus it, type value, submit search
            HtmlTextInput cyclistSearchInput = page.querySelector("body > div.wrapper > div.content > div:nth-child(2) > form > input[type=text]:nth-child(1)");
            cyclistSearchInput.select();
            cyclistSearchInput.type(nameOfCyclist);
            HtmlSubmitInput cyclistSearch = page.querySelector("body > div.wrapper > div.content > div:nth-child(2) > form > input[type=submit]:nth-child(2)");
            page = cyclistSearch.click();

            // find anchor element with href(link?) and click on it
            HtmlAnchor cyclist = page.querySelector("body > div.wrapper > div.content > div:nth-child(6) > a");

            if (cyclist != null) {
                page = cyclist.click();
            } else {
                System.out.println("Cyclist with this name could not be found");
                throw new Exception();
            }

            // find dropdown box element, hover it with mouse, find desired option, click on it
            HtmlElement statistics = page.querySelector("body > div.wrapper > div.content > div.page-topnav > ul > li:nth-child(2) > a");

            if (statistics != null) {
                page = (HtmlPage) statistics.mouseOver();
            } else {
                System.out.println("Cyclist with this name could not be found");
                throw new Exception();
            }

            page = ((HtmlAnchor) page.querySelector("body > div.wrapper > div.content > div.page-topnav > ul > li:nth-child(2) > ul > li:nth-child(2) > a")).click();

            // find dropdown box filter element, select desired filter, submit filter
            HtmlSelect select = page.querySelector("body > div.wrapper > div.content > div.page-content > div.page-object.default > form > ul > li:nth-child(2) > span.input > select");
            HtmlOption option = select.getOptionByValue("stages");
            select.setSelectedAttribute(option, true);
            HtmlSubmitInput filter = page.querySelector("body > div.wrapper > div.content > div.page-content > div.page-object.default > form > ul > li:nth-child(5) > span.input > input");
            page = filter.click();

            // get list of elements with data from the table
            DomNodeList<DomNode> rows;

            if ((rows = page.querySelector("body > div.wrapper > div.content > div.page-content > div.page-object.default > div.tableCont > table > tbody").getChildNodes()).size() > 0) {

                boolean selectedRaceCatWin = false;

                // loop through the list, print out number, date and name of the race, but only for races that are class 2.1
                for (DomNode row : rows) {
                    if (row.querySelector("tr > td:nth-child(3)").getTextContent().equals("2.1")) {

                        selectedRaceCatWin = true;

                        System.out.println("Nr.: "
                                + row.querySelector(row.getNodeName() + " > td:nth-child(1)").getTextContent()
                                + "   | Date: "
                                + row.querySelector(row.getNodeName() + " > td:nth-child(4)").getTextContent()
                                + "   | Race: "
                                + row.querySelector(row.getNodeName() + " > td:nth-child(2)").getTextContent());
                    }
                }

                if (!selectedRaceCatWin)
                    System.out.println("This cyclist doesn't have this kind of a stage win.");
            } else {
                System.out.println("This cyclist doesn't have any stage wins");
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}
