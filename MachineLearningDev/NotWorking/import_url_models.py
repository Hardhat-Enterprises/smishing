# Load model directly
from transformers import AutoTokenizer, AutoModelForSequenceClassification, BertTokenizer

# Not working, something wrong with torch but don't know how to fix
# Import url model and tokenizer from transformers

tokenizer = AutoTokenizer.from_pretrained("kmack/malicious-url-detection")
model = AutoModelForSequenceClassification.from_pretrained("kmack/malicious-url-detection")

sample_messages = [
    "Hello, you still have a fine that has not been paid. Please pay it in time, otherwise it will affect your travel. https://linkstps.xyz/au",
    "Eastlink: There is an outstanding debt on the toll invoice. Settlement should always be made before the maturity datae. https://tolls.eastlink.click/online",
    "We're unable to deliver your online package due to an address error. Please click promptly to update the address for re-delivery. https://ausorriso.xyz/i",
    "TODAY ONLY 40% off traditional and premium pizzas* ORDER NOW dominoes.au/7MKXrbmdRk T&Cs apply. To opt out send STOP to 0485865365",
    "Chemist Warehouse Fountain Gate sent you a Slyp receipt. View it here: https://reciepts.slyp.com.au/WRA-b94511379edf4de481438e-f4a6e952c0",
    "[DiDi]$15 off next 2 rides!* Savings automatically applied on your next ride request. Until Sunday. https://dd.me/k3M7s23 Opt out: https://dd.me/b6kHCl5",
    "JUST ANNOUNCED Big Apple $750 Scholarship Study in New York State (USA) this July. 4-week program designed for Australian Uni students! www.cisaustalia.com.au"
]

for message in sample_messages:
# Initialise empty predictions list
    all_predictions = []
    print(f"Message: {message}")
    inputs = tokenizer(message, return_tensors="pt")
    outputs = model(**inputs)

    print_results(outputs)
