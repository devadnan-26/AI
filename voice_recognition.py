import pandas as pd
import time
import pyttsx3
import os
import cv2
import face_recognition
import numpy as np
import pyaudio
import wave
import pickle
from chat import ChatBot
from scipy.io.wavfile import read
import speech_recognition as sr
from sklearn import preprocessing
from sklearn.mixture import GaussianMixture
import python_speech_features as mfcc
from googletrans import Translator
import transformers

waves_names = []
all_face_encodings = {}
bool_responses = ["yes", "true", "yup", "yeah"]
retry = 0
data = pd.read_csv("names.csv")
with open("names.txt", "r") as f:
    names = [line.rstrip('\n') for line in f]
with open("training_voices.txt", "r") as f:
    waves_names = [line.rstrip('\n') for line in f]
with open("dataset_faces.dat", "rb") as f:
    all_face_encodings = pickle.load(f)
face_names = list(all_face_encodings.keys())
face_encodings = np.array(list(all_face_encodings.values()))
informations = list(zip(data.name, data.birthday))
r = sr.Recognizer()


def speak(command):
    engine = pyttsx3.init('sapi5')
    voices = engine.getProperty('voices')
    engine.setProperty('voice', voices[0].id)
    engine.say(command)
    engine.runAndWait()


def show_webcam():
    faceCascade = cv2.CascadeClassifier(
        cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
    cam = cv2.VideoCapture(0)
    img_item = "filename.jpg"
    et_val, frame = cam.read()
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = faceCascade.detectMultiScale(gray, 1.1, 4)
    for (x, y, w, h) in faces:
        roi_gray = frame[y: y + h, x: x + w]
        cv2.rectangle(frame, (x, y), (x+w, y+h), (0, 255, 0), 2)
        cv2.imwrite(img_item, roi_gray)
    img_encoding = face_recognition.face_encodings(
        face_recognition.load_image_file(img_item))[0]
    results = face_recognition.compare_faces(face_encodings, img_encoding)
    if True in results:
        index = results.index(True)
        name = face_names[index]
    else:
        name = ""
    return name


def capture_image(name):
    with open("images.txt", "r") as f:
        images = [line.rstrip("\n") for line in f]
    faceCascade = cv2.CascadeClassifier(
        cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
    cam = cv2.VideoCapture(0)
    img_item = f"{name}.jpg"
    et_val, frame = cam.read()
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = faceCascade.detectMultiScale(gray, 1.1, 4)
    if img_item not in images:
        for (x, y, w, h) in faces:
            roi_gray = frame[y: y + h, x: x + w]
            cv2.rectangle(frame, (x, y), (x+w, y+h), (0, 255, 0), 2)
            cv2.imwrite(img_item, roi_gray)
        encoding = face_recognition.face_encodings(
            face_recognition.load_image_file(img_item))[0]
        all_face_encodings[f"{name}"] = encoding
        with open("dataset_faces.dat", "wb") as f:
            pickle.dump(all_face_encodings, f)


def record(name):
    for count in range(3):
        FORMAT = pyaudio.paInt16
        CHANNELS = 1
        RATE = 44100
        CHUNK = 512
        RECORD_SECONDS = 10
        audio = pyaudio.PyAudio()
        if count == 0:
            print("Ok! Now I will record the first file")
            speak("Ok! Now I will record the first file")
        elif count == 1:
            print("Ok! Now I will record the second file")
            speak("Ok! Now I will record the second file")
        else:
            print("Ok! Now I will record the third file")
            speak("Ok! Now I will record the third file")
        print("recording via index "+str(1))
        stream = audio.open(format=FORMAT, channels=CHANNELS,
                            rate=RATE, input=True, input_device_index=1,
                            frames_per_buffer=CHUNK)
        print("recording started")
        Recordframes = []
        for i in range(0, int(RATE / CHUNK * RECORD_SECONDS)):
            data = stream.read(CHUNK)
            Recordframes.append(data)
        print("recording stopped")
        stream.stop_stream()
        stream.close()
        audio.terminate()
        OUTPUT_FILENAME = name+"-sample"+str(count)+".wav"
        WAVE_OUTPUT_FILENAME = os.path.join("training_set", OUTPUT_FILENAME)
        trainedfilelist = open("training_voices.txt", 'a')
        with open("training_voices.txt", "r") as f:
            if OUTPUT_FILENAME not in f:
                trainedfilelist.write(OUTPUT_FILENAME+"\n")
        waveFile = wave.open(WAVE_OUTPUT_FILENAME, 'wb')
        waveFile.setnchannels(CHANNELS)
        waveFile.setsampwidth(audio.get_sample_size(FORMAT))
        waveFile.setframerate(RATE)
        waveFile.writeframes(b''.join(Recordframes))
        waveFile.close()


def record_test():
    exist = open("testing_voices.txt", "r")
    FORMAT = pyaudio.paInt16
    CHANNELS = 1
    RATE = 44100
    CHUNK = 512
    RECORD_SECONDS = 10
    audio = pyaudio.PyAudio()
    print("recording via index "+str(1))
    stream = audio.open(format=FORMAT, channels=CHANNELS,
                        rate=RATE, input=True, input_device_index=1,
                        frames_per_buffer=CHUNK)
    print("recording started")
    Recordframes = []
    for i in range(0, int(RATE / CHUNK * RECORD_SECONDS)):
        data = stream.read(CHUNK)
        Recordframes.append(data)
    print("recording stopped")
    stream.stop_stream()
    stream.close()
    audio.terminate()
    OUTPUT_FILENAME = "test.wav"
    WAVE_OUTPUT_FILENAME = os.path.join("testing_set", OUTPUT_FILENAME)
    if OUTPUT_FILENAME in exist:
        trainedfilelist = open("testing_voices.txt", 'a')
        trainedfilelist.write(OUTPUT_FILENAME+"\n")
    waveFile = wave.open(WAVE_OUTPUT_FILENAME, 'wb')
    waveFile.setnchannels(CHANNELS)
    waveFile.setsampwidth(audio.get_sample_size(FORMAT))
    waveFile.setframerate(RATE)
    waveFile.writeframes(b''.join(Recordframes))
    waveFile.close()


def calculate_delta(array):
    rows, cols = array.shape
    print(rows)
    print(cols)
    deltas = np.zeros((rows, 20))
    N = 2
    for i in range(rows):
        index = []
        j = 1
        while j <= N:
            if i-j < 0:
                first = 0
            else:
                first = i-j
            if i+j > rows-1:
                second = rows-1
            else:
                second = i+j
            index.append((second, first))
            j += 1
        deltas[i] = (array[index[0][0]]-array[index[0][1]] +
                     (2 * (array[index[1][0]]-array[index[1][1]]))) / 10
    return deltas


def extract_features(audio, rate):

    mfcc_feature = mfcc.mfcc(audio, rate, 0.025, 0.01,
                             20, nfft=1200, appendEnergy=True)
    mfcc_feature = preprocessing.scale(mfcc_feature)
    print(mfcc_feature)
    delta = calculate_delta(mfcc_feature)
    combined = np.hstack((mfcc_feature, delta))
    return combined


def train_model():
    source = "C:\\Users\\adnan\\Desktop\\Voice Identification\\training_set\\"
    dest = "C:\\Users\\adnan\\Desktop\\Voice Identification\\trained_models\\"
    train_file = "C:\\Users\\adnan\\Desktop\\Voice Identification\\training_voices.txt"
    file_paths = open(train_file, 'r')
    count = 1
    features = np.asarray(())
    for path in file_paths:
        path = path.strip()
        print(path)

        sr, audio = read(source + path)
        print(sr)
        vector = extract_features(audio, sr)

        if features.size == 0:
            features = vector
        else:
            features = np.vstack((features, vector))

        if count == 3:
            gmm = GaussianMixture(
                n_components=6, max_iter=200, covariance_type='diag', n_init=3)
            gmm.fit(features)

            # dumping the trained gaussian model
            picklefile = path.split("-")[0]+".gmm"
            pickle.dump(gmm, open(dest + picklefile, 'wb'))
            print('+ modeling completed for speaker:', picklefile,
                  " with data point = ", features.shape)
            features = np.asarray(())
            count = 0
        count = count + 1


def test_model():

    source = "C:\\Users\\adnan\\Desktop\\Voice Identification\\testing_set\\"
    modelpath = "C:\\Users\\adnan\\Desktop\\Voice Identification\\trained_models\\"
    test_file = "C:\\Users\\adnan\\Desktop\\Voice Identification\\testing_voices.txt"
    file_paths = open(test_file, 'r')

    gmm_files = [os.path.join(modelpath, fname) for fname in
                 os.listdir(modelpath) if fname.endswith('.gmm')]

    # Load the Gaussian gender Models
    models = [pickle.load(open(fname, 'rb')) for fname in gmm_files]
    speakers = [fname.split("\\")[-1].split(".gmm")[0] for fname
                in gmm_files]

    # Read the test directory and get the list of test audio files
    for path in file_paths:

        path = path.strip()
        print(path)
        sr, audio = read(source + path)
        vector = extract_features(audio, sr)

        log_likelihood = np.zeros(len(models))

        for i in range(len(models)):
            gmm = models[i]  # checking with each model one by one
            scores = np.array(gmm.score(vector))
            log_likelihood[i] = scores.sum()

        winner = np.argmax(log_likelihood)
        print("\tdetected as - ", speakers[winner])
        time.sleep(1.0)


def record_new_name(response):
    source = sr.Microphone()
    birthdays = data.iloc[:, 1].values
    name_results = []
    try:
        if response in names:
            print(
                f"You must prove that you are {response}, so, I need few informations from you")
            speak(
                f"You must prove that you are {response}, so, I need few informations from you")
            speak("When is your birthday?")
            with source:
                audio = r.listen(source)
            try:
                information = r.recognize_google(audio)
            except sr.UnknownValueError:
                while True:
                    speak("Sorry, come again?")
                    with source:
                        audio = r.listen(source)
                        try:
                            information = r.recognize_google(audio)
                            break
                        except Exception as e:
                            None
            except sr.RequestError:
                print(
                    "Could not request results from Google Speech Recognition service; {0}".format(e))
            for i in range(len(birthdays)):
                if information in informations[i][1]:
                    speak(
                        f"Ok {response}! Let me save your voice's waves to recognize your voice later.")
                    speak(
                        "I'll wait you until you say I am ready, then, I'll record your voice. You can say anything you want.")
                    name_results.append(True)
                    capture_image(response)
                    with source:
                        audio = r.listen(source)
                    try:
                        ready_response = r.recognize_google(audio)
                        if "I am ready" in ready_response:
                            record(response)
                            train_model()
                            break
                        else:
                            print("Sorry! I didn't understand. Come again?")
                            speak("Sorry! I didn't understand. Come again?")
                            with source:
                                while True:
                                    audio = r.listen(source)
                                    ready_response = r.recognize_google(audio)
                                    if "I am ready" in ready_response:
                                        record(response)
                                        train_model()
                                        break
                                break
                    except sr.UnknownValueError:
                        while True:
                            print("Sorry! I didn't understand. Come again?")
                            speak("Sorry! I didn't understand. Come again?")
                            with source:
                                audio = r.listen(source)
                            try:
                                ready_response = r.recognize_google(audio)
                                if "I am ready" in ready_response:
                                    record(response)
                                    train_model()
                                    break
                            except Exception as e:
                                None
                    except sr.RequestError as e:
                        print(
                            "Could not request results from Google Speech Recognition service; {0}".format(e))
                else:
                    name_results.append(False)
                    speak(
                        "I'm sorry, but your informations are wrong. Closing the system...")
                    print(
                        "I'm sorry, but your informations are wrong. Closing the system...")
            if False in name_results:
                if True not in name_results:
                    exit()
        else:
            print(f"Hi {response}! How can I help you?")
            speak(f"Hi {response}! How can I help you?")
            with source:
                audio = r.listen(source)
    except sr.UnknownValueError:
        x = "Sorry! I didn't understand."
        print(x)
        speak(x)
    except sr.RequestError as e:
        print(
            "Could not request results from Google Speech Recognition service; {0}".format(e))


def name(name):
    if name in names:
        print(f"Hi {name}! How are you today?")
        speak(f"Hi {name}! How are you today?")
        ai = ChatBot(name="Çelebi")
        nlp = transformers.pipeline(
            "conversational", model="microsoft/DialoGPT-medium")
        os.environ["TOKENIZERS_PARALLELISM"] = "true"
        ex = True
        while ex:
            ai.speech_to_text()
            translator = Translator()
            if ai.wake_up(str(ai.text)) is True:
                res = "Merhaba ben Robot Çelebi, size nasıl yardımcı olabilirim?"
            elif "time" in str(ai.text):
                res = ai.action_time()
            elif any(i in str(ai.text) for i in ["thank", "thanks"]):
                res = np.random.choice(["rica ederim!", "istediğin zaman!", "Sorun değil!",
                                       "Harika!", "Bana ihtiyacın olursa buradayım!", "ondan bahsetme"])
            elif any(i in str(ai.text) for i in ["exit", "close"]):
                res = np.random.choice(["görüşmek üzere", "İyi günler", "Hoşçakal",
                                       "Güle güle", "görüşürüz", "Allah'a emanet", "Allah emanet ol"])
                ex = False
            else:
                if str(ai.text) == "ERROR":
                    res = "Bardon, tekrar edebilirmisiniz?"
                else:
                    chat = nlp(transformers.Conversation(
                        str(ai.text)), pad_token_id=50256)
                    res = str(chat)
                    res = res[res.find("bot >> ")+6:].strip()
                    res = translator.translate(
                        text=str(res), src="en", dest="tr").text
            ai.text_to_speech(res)
        print("----- Closing down Dev -----")
    else:
        print("Hi! How can I help you?")
        speak("Hi! How can I help you?")


def new_name():
    source = sr.Microphone()
    with source:
        audio = r.listen(source)
        try:
            response = r.recognize_google(audio)
            response = str(response)
            print(response)
            if "my name is " or "I am " or "I'm " in response:
                if "my name is " in response:
                    response = response.replace("my name is ", "")
                elif "I am " in response:
                    response = response.replace("I am ", "")
                else:
                    response = response.replace("I'm ", "")
            x = "I think your name is: " + response
            print(f"{x}\n Is that right?")
            speak(f"{x}\n Is that right?")
            audio = r.listen(source)
            bool_response = r.recognize_google(audio)
            bool_response = str(bool_response)
            print(bool_response)
            for i in bool_responses:
                if i in bool_response:
                    # with open("names.txt", "a") as f:
                    #     if response not in f:
                    #         f.write(f"{response}\n")
                    record_new_name(response)
                    break
                else:
                    while True:
                        try:
                            print("Sorry! Come again?")
                            speak("Sorry! Come again?")
                            audio = r.listen(source)
                            response = r.recognize_google(audio)
                            response = str(response)
                            print(response)
                            if "my name is " or "I am " or "I'm " in response:
                                if "my name is " in response:
                                    response = response.replace(
                                        "my name is ", "")
                                elif "I am " in response:
                                    response = response.replace("I am ", "")
                                else:
                                    response = response.replace("I'm ", "")
                        except sr.UnknownValueError:
                            x = "Sorry! I didn't understand."
                            print(x)
                            speak(x)
                        except sr.RequestError as e:
                            print(
                                "Could not request results from Google Speech Recognition service; {0}".format(e))
                        x = "I think your name is: " + response
                        print(f"{x}\n Is that right?")
                        speak(f"{x}\n Is that right?")
                        audio = r.listen(source)
                        bool_response = r.recognize_google(audio)
                        bool_response = str(bool_response)
                        print(bool_response)
                        if i in bool_response:
                            with open("names.txt", "a") as f:
                                f.write(f"{response}\n")
                            record_new_name(response)
                            break
                        break
        except sr.UnknownValueError:
            x = "Sorry! I didn't understand."
            print(x)
            speak(x)
        except sr.RequestError as e:
            print(
                "Could not request results from Google Speech Recognition service; {0}".format(e))


while True:
    if show_webcam() == "":
        speak("Hi! I'm Robot Chelebi. What's your name?")
        new_name()
        break
    else:
        name(show_webcam())
        break
