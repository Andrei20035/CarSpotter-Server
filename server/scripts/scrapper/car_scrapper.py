import csv

from selenium import webdriver
from selenium.common import TimeoutException
from selenium.webdriver.edge.options import Options
from selenium.webdriver.edge.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.microsoft import EdgeChromiumDriverManager
from bs4 import BeautifulSoup
import time
from tqdm import tqdm
from datetime import datetime

options = Options()
# options.add_argument("--headless")
driver = webdriver.Edge(service=Service(EdgeChromiumDriverManager().install()), options=options)

driver.get("https://www.autoevolution.com/cars/")
try:
    agree_button = WebDriverWait(driver, 20).until(
        EC.presence_of_element_located((By.ID, "accept-btn"))
    )
    agree_button.click()
    print("Privacy popup accepted")
except TimeoutException:
    print("Timeout: brand list not found.")

soup = BeautifulSoup(driver.page_source, "html.parser")

brand_section = soup.select_one("div.container.carlist.clearfix")

brands = []
if brand_section:
    brand_divs = brand_section.find_all("div", class_="col2width fl bcol-white carman")

    for div in tqdm(brand_divs, desc="Scraping brands"):
        brand_name = div.find("span", itemprop="name").text.strip()
        brand_url = div.find("h5").find("a")["href"]
        brands.append((brand_name, brand_url))

    for brand in brands:
        print(brand[0] + " " + brand[1])
else:
    print("Carlist not found.")

brand_models = {}

for brand_name, brand_url in tqdm(brands, desc="Scraping models"):
    print(f"Scraping models for brand: {brand_name}")
    driver.get(brand_url)
    time.sleep(3)
    brand_soup = BeautifulSoup(driver.page_source, "html.parser")

    containers = brand_soup.find_all("div", class_="carmodels col23width clearfix")

    models = []
    for container in containers:
        model_elements = container.find_all("div", class_="carmod clearfix")
        for model_div in model_elements:
            model_name_tag = model_div.find("h4")
            sibling_div = model_div.find("div", class_="col3width fl")

            if model_name_tag:
                model_name = model_name_tag.text.strip()

                start_year = None
                end_year = None

                if sibling_div:
                    years_span = sibling_div.find("span")
                    if years_span:
                        years_text = years_span.text.strip()

                        if years_text:
                            years = [y.strip() for y in years_text.split("-")]

                            start_year = years[0] if len(years) > 0 else None
                            if len(years) > 1:
                                if years[1].upper() == "PRESENT":
                                    end_year = datetime.today().year
                                else:
                                    end_year = years[1]
                            else:
                                end_year = None

                models.append((model_name[len(brand_name):].strip(), start_year, end_year))
                tqdm.write(f"  - {model_name} {start_year} - {end_year}")

    brand_models[brand_name] = models

driver.quit()

with open("cars.csv", "w", newline="", encoding="utf-8") as cars_file:
    writer = csv.writer(cars_file)
    writer.writerow(["Brand", "Model", "Start Year", "End Year"])

    for brand, models in brand_models.items():
        for model_tuple in models:
            model_name, start_year, end_year = model_tuple
            writer.writerow([brand.capitalize(), model_name.capitalize(), start_year, end_year])
