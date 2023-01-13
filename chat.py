import speech_recognition as sr
from gtts import gTTS
from googletrans import Translator
import transformers
import pyttsx3
import os
import time
import os
import datetime
import numpy as np

class ChatBot():
    global engine
    engine = pyttsx3.init('sapi5')
    voices = engine.getProperty('voices')
    engine.setProperty('voice', voices[0].id)
    def __init__(self, name):
        print("----- Starting up", name, "-----")
        self.name = name

    def speech_to_text(self):
        recognizer = sr.Recognizer()
        translator = Translator()
        from_lang = 'tr'
        to_lang = 'en'
        with sr.Microphone() as mic:
            print("Listening...")
            audio = recognizer.listen(mic)
            self.text="ERROR"
        try:
            self.text = recognizer.recognize_google(audio, language="tr")
            self.text = translator.translate(text=self.text, src=from_lang, dest=to_lang).text
            print("Me  --> ", self.text)
        except:
            print("Me  -->  ERROR")
            
    @staticmethod
    def text_to_speech(text):
        print("Dev --> ", text)
        speaker = gTTS(text=text, lang="tr", slow=False).text
        engine.say(speaker)
        engine.runAndWait()
        

    def wake_up(self, text):
        return True if self.name in str(text).lower() else False

    @staticmethod
    def action_time():
        return datetime.datetime.now().time().strftime('%H:%M')

if __name__ == "__main__":
    
    ai = ChatBot(name="Çelebi")
    nlp = transformers.pipeline("conversational", model="microsoft/DialoGPT-large")
    os.environ["TOKENIZERS_PARALLELISM"] = "true"
    ex=True
    while ex:
        ai.speech_to_text()
        translator = Translator()
        if ai.wake_up(str(ai.text)) is True:
            res = "Merhaba ben Robot Çelebi, size nasıl yardımcı olabilirim?"
        elif "time" in str(ai.text):
            res = ai.action_time()
        elif any(i in str(ai.text) for i in ["thank","thanks"]):
            res = np.random.choice(["rica ederim!","istediğin zaman!","Sorun değil!","Harika!","Bana ihtiyacın olursa buradayım!","ondan bahsetme"])
        elif any(i in str(ai.text) for i in ["exit","close"]):
            res = np.random.choice(["görüşmek üzere","İyi günler","Hoşçakal","Güle güle","görüşürüz", "Allah'a emanet", "Allah emanet ol"])
            ex=False
        else:   
            if str(ai.text)=="ERROR":
                res="Bardon, tekrar edebilirmisiniz?"
            else:
                chat = nlp(transformers.Conversation(str(ai.text)), pad_token_id=50256)
                res = str(chat)
                res = res[res.find("bot >> ")+6:].strip()
                res = translator.translate(text=str(res), src="en", dest="tr")
        ai.text_to_speech(res.text)
    print("----- Closing down Dev -----")