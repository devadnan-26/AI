import pandas as pd
import pickle
data = pd.read_csv("names.csv")
informations = list(zip(data.name, data.birthday))
birthdays = data.iloc[:, 0].values
print(birthdays)