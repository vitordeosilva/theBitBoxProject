import pyglet



sprite = pyglet.sprite.Sprite(pyglet.resource.animation('anim.gif'))

H_ratio = max(sprite.height) / min(sprite.height)
W_ratio = max(sprite.width) / min(sprite.width)

sprite.scale = min(H_ratio, W_ratio) # sprite.scale = 2 would double the size.
                                     # We'll upscale to the lowest of width/height
                                     # to not go out of bounds. Whichever
                                     # value hits the screen edges first essentially.

window = pyglet.window.Window(width=monitor.width, height=monitor.height, fullscreen=True)

pyglet.gl.glClearColor(1, 1, 1, 1)

@window.event
def on_draw():
    window.clear()
    sprite.draw()

pyglet.app.run()