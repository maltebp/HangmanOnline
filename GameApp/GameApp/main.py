##############################################################################################################
#                                         __                                                                 #
#                   /   /   __      _    /  \     /|  /|     __       _                                      #
#                  /---/  /   |  |/  \   \__|    / |/  |   /   |   |/  \                                     #
#                 /   /   \__/|  |   |      |   /      |   \__/|   |   |                                     #
#                                        \__/                                                                #
#      Great help and example snippets from: https://www.101computing.net/getting-started-with-pygame/       #
##############################################################################################################
from GameApp import Gallow
import sys
import zeep
import pygame
pygame.init()

username, password = sys.argv[1], sys.argv[2]


COLOUR_BACKGROUND = (221, 110, 56)
COLOUR_FOREGROUND = (221, 192, 56)
font_gameover = pygame.font.Font('freesansbold.ttf', 64)
font_word = pygame.font.Font('freesansbold.ttf', 32)
font_guess = pygame.font.Font('freesansbold.ttf', 16)


def login() -> zeep.Client:
    client = zeep.Client(wsdl="http://maltebp.dk:9902/hangmanlogic?wsdl")
    client.service.logout(username, password)
    client.service.login(username, password)
    return client


# -------- Main Game Loop -----------

# Open a new window
size = (700, 500)
screen = pygame.display.set_mode(size)
pygame.display.set_caption("My First Game")

# Calculate some important size parameters
width = 700
offset = round(width/5)
middle = offset * 3
divWidth = round(middle / 4)


gallow = Gallow.Gallow(screen.get_rect().midtop[0] - 85, 25)
spriteList = pygame.sprite.Group()
spriteList.add(gallow)

# The loop will carry on until the user exit the game (e.g. clicks the close button).
carryOn = True
# The clock will be used to control how fast the screen updates
clock = pygame.time.Clock()

# access server
gameServer = login()
# start game on server
gamestate = gameServer.service.startGame(username)
print("Game state: ")
print(gamestate)


key = ''
gameover_text = font_gameover.render("", True, (200, 0, 0))

while carryOn:
    # --- Main event loop

    # --- Game logic should go here
    for event in pygame.event.get():  # User did something
        if event.type == pygame.QUIT:  # If user clicked close
            carryOn = False  # Flag that we are done so we exit this loop
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_RETURN and key != '':
                print("Guess: '" + key + "' sent to server")
                gamestate = gameServer.service.guessLetter(username, ord(key[0]))
                print(gamestate)
                key = ''

                if gamestate.gameFinished:
                    if gamestate.gameWon:
                        gameover_text = font_gameover.render("You Won!", True, (200, 0, 0))
                    else:
                        gameover_text = font_gameover.render("You lost!", True, (200, 0, 0))


            else:
                print("A key was pressed: ", event.unicode)
                key = event.unicode



    spriteList.update()
    word = font_word.render(gamestate.currentWord, True, (0, 0, 0))
    guess = font_guess.render("You guessed: '" + key + "', press enter to accept", True, (0, 0, 0))

    # --- Drawing code should go here
    # First, clear the screen to white.
    screen.fill(COLOUR_BACKGROUND)


    # The you can draw different shapes and lines or add text to your background stage.
    pygame.draw.rect(screen, COLOUR_FOREGROUND, [offset, 0, middle, 500])
    for i in range(5):
        pygame.draw.line(screen, (255, 255, 255), [offset + divWidth * i, 0], [offset + divWidth * i, 500], 5)

    # Add the gallow to the game
    spriteList.draw(screen)

    # Add text
    screen.blit(word, (screen.get_rect().center[0] - 100, screen.get_rect().center[1]))  # write text
    screen.blit(guess, (screen.get_rect().center[0] - 150, screen.get_rect().center[1] + 50))  # write text
    screen.blit(gameover_text, screen.get_rect().center)
    # --- Go ahead and update the screen with what we've drawn.
    pygame.display.flip()

    # --- Limit to 60 frames per second
    clock.tick(60)

# Once we have exited the main program loop we can stop the game engine:
pygame.quit()


