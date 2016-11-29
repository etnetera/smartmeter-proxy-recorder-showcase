SmartMeter.io Smart proxy recorder showcase
-------------------------------------------
Demonstrates the capabilities of the SmartMeter.io [Smart proxy recorder](https://www.smartmeter.io/documentation#toc-smart-proxy-recorder).
It contains [Selenium WebDriver](http://www.seleniumhq.org/projects/webdriver/) test enhanced with requests to Smart proxy recorder.

To follow the showcase:
-----------------------
1. Clone this repository.
1. Download and install [SmartMeter.io](https://www.smartmeter.io/download).
1. Copy all files from *smartmeter/tests* folder from this repo into *tests* folder in installed SmartMeter.io.
1. Run SmartMeter.io and open scenario *academy-recorder.jmx*.
1. Start Smart proxy recorder.
1. Run tests in this Java project using Maven or your preferred IDE.
 
You should see updated *tests/academy-from-selenium.jmx* scenario in installed SmartMeter.io.

Note:
-------------------

Showcase was prepared for SmartMeter.io 1.2.0 version.

Java tests runs Selenium WebDriver 2.53.1 version which is compatible with Firefox 47. For new Firefox version use updated version of Selenium WebDriver.
Do not use Selenium WebDriver 3.0.1 as it needs to use [geckodriver](https://github.com/mozilla/geckodriver) and its latest version has issues with setting the proxy.
