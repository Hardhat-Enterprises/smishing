The dataset will look something like this:

Example:
Spam.com------- Phishing;
Google.com----- Harmless;
downloadme.com----Malware;
Youtube.com---Harmless;

You may also modify the code if you are working on feature extraction or machine learning model training. I have excluded some additional data to streamline the process, but it can be reintegrated with modifications to the code as needed.

Please note that the dataset is too large to be uploaded directly to Git. To obtain the dataset, run the script_to_gather_real_world_phishing_urls.ipynb file. The script will generate the dataset, which may take some time depending on your device's processing capabilities. The dataset contains approximately 700,000 URLs and could exceed one million URLs.

Each URL in the dataset is accurately labeled (e.g., Harmless, Malware, etc.). For detailed information on the dataset's structure and usage, please refer to the overview section within the script_to_gather_real_world_phishing_urls.ipynb file.

Data Sources:
- URLHaus: Known for its extensive collection of URLs associated with malware and other threats.
- PhishStats: Specializes in tracking phishing URLs.
- CommonCrawl: Provides a vast dataset of harmless URLs, ensuring balanced data for testing.

Running the code will output data from the past 6 months which will guarantee that you have up-to-date data at all times.
