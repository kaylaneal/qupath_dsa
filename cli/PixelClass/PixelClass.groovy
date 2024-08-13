import static qupath.lib.gui.scripting.QPEx.*

// Load Image
def imageData = getCurrentImageData()
def server = imageData.getServer()

def meta = server.getMetadata()
println meta
