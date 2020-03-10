##############################################################################################################
#                                         __                                                                 #
#                   /   /   __      _    /  \     /|  /|     __       _                                      #
#                  /---/  /   |  |/  \   \__|    / |/  |   /   |   |/  \                                     #
#                 /   /   \__/|  |   |      |   /      |   \__/|   |   |                                     #
#                                        \__/                                                                #
#      Great help and example snippets from: https://www.101computing.net/getting-started-with-pygame/       #
##############################################################################################################
from GameApp import Gallow
import unicodedata
import pygame
pygame.init()

COLOUR_BACKGROUND = (221, 110, 56)
COLOUR_FOREGROUND = (221, 192, 56)

# Open a new window
size = (700, 500)
screen = pygame.display.set_mode(size)
pygame.display.set_caption("My First Game")


gallow = Gallow.Gallow(700/2-85, 50)
spriteList = pygame.sprite.Group()
spriteList.add(gallow)

# The loop will carry on until the user exit the game (e.g. clicks the close button).
carryOn = True
# The clock will be used to control how fast the screen updates
clock = pygame.time.Clock()


# -------- Main Program Loop -----------
while carryOn:
    # --- Main event loop
    for event in pygame.event.get():  # User did something
        if event.type == pygame.QUIT:  # If user clicked close
            carryOn = False  # Flag that we are done so we exit this loop

        if event.type == pygame.KEYDOWN:
            print("A key was pressed: ", event.unicode)
            # some_soap_method(event.unicode)
            gallow.increment()

    # keys = pygame.key.get_pressed()
    # print("Key pressed: ", keys)

    # --- Game logic should go here
    spriteList.update()

    # --- Drawing code should go here
    # First, clear the screen to white.
    screen.fill(COLOUR_BACKGROUND)
    width = 700
    offset = width/5
    middle = offset * 3
    divWidth = middle / 4
    # The you can draw different shapes and lines or add text to your background stage.
    pygame.draw.rect(screen, COLOUR_FOREGROUND, [offset, 0, middle, 500])
    for i in range(5):
        pygame.draw.line(screen, (255, 255, 255), [offset + divWidth * i, 0], [offset + divWidth * i, 500], 5)

    # Add the gallow to the game
    spriteList.draw(screen)

    # --- Go ahead and update the screen with what we've drawn.
    pygame.display.flip()

    # --- Limit to 60 frames per second
    clock.tick(60)

# Once we have exited the main program loop we can stop the game engine:
pygame.quit()
