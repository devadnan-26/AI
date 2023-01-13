from chat import *
from kivymd.app import MDApp
from kivymd.uix.bottomnavigation import MDBottomNavigation
from kivymd.uix.button import *
from kivymd.uix.boxlayout import MDBoxLayout
from kivy.lang.builder import Builder
from kivymd.uix.screen import MDScreen
from kivy.uix.image import Image, AsyncImage

KV = '''
MDBottomNavigation:
    panel_color: .2, .2, .2, 1

    MDBottomNavigationItem:
        name: 'HomeScreen'
        text: 'Home'
        icon: 'home'

        Image:
            source: 'sources/robot image.png'
            size: self.texture_size

    MDBottomNavigationItem:
        name: 'PlantsScreen'
        text: 'Plants'
        icon: 'carrot'

        MDLabel:
            text: 'Plants'
            halign: 'center'

    MDBottomNavigationItem:
        name: 'TasksScreen'
        text: 'Tasks'
        icon: 'note-multiple'

        MDLabel:
            text: 'Tasks'
            halign: 'center'

    MDBottomNavigationItem:
        name: 'WikiScreen'
        text: 'Wiki'
        icon: 'web'

        MDLabel:
            text: 'Wiki'
            halign: 'center'

'''



class main(MDApp):
    def build(self):
        return Builder.load_string(KV)




if __name__ == '__main__':
    main().run()