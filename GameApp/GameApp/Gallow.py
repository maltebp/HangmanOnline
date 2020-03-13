import pygame

WHITE = (255, 255, 255)


class Gallow(pygame.sprite.Sprite):
    # This class represents a car. It derives from the "Sprite" class in Pygame.
    __wrong__ = 0

    def __init__(self, x, y):
        # Call the parent class (Sprite) constructor
        super().__init__()

        # Instead we could load a proper pciture of a car...
        self.image = pygame.image.load("pkg_resources/images/wrong0.png")

        # Fetch the rectangle object that has the dimensions of the image.
        self.rect = self.image.get_rect()
        self.rect.x = x
        self.rect.y = y

        self.__wrong__ = 0

    def init_with_wrongs(self, x, y, wrongs):
        # Call the parent class (Sprite) constructor
        super().__init__()
        self.__init__(x, y)

        if not isinstance(wrongs, int):
            raise TypeError("bar must be set to an integer")
        if not wrongs >= 0 & wrongs < 7:
            return

        # Instead we could load a proper pciture of a car...
        self.image = pygame.image.load("pkg_resources/images/wrong" + wrongs.__str__() + ".png")

        # Fetch the rectangle object that has the dimensions of the image.
        self.rect = self.image.get_rect()

        self.__wrong__ = wrongs

    def increment(self):
        self.__wrong__ += 1
        try:
            # Instead we could load a proper pciture of a car...
            self.image = pygame.image.load("pkg_resources/images/wrong" + self.__wrong__.__str__() + ".png")

        except:
            print("couldnt find: " + "pkg_resources/images/wrong" + self.__wrong__.__str__() + ".png")

