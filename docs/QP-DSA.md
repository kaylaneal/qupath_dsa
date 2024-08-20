# QuPath CLI for DSA

## Set Up
The **QuPath-DSA** Plugin Dockerfile is located in the [base directory](../Dockerfile)
- QuPath for Linux is expected to be downloaded in the project directory *BEFORE* building Docker image.
    - QuPath Downloading Instructions: [QuPath GitHub](https://github.com/qupath/qupath/releases/tag/v0.5.1)
        - *Working on ideas to include in Dockerfile itself so this step is unnecessary*
    - Locally saved to `qpbin` folder within base directory


## Writing CLI Programs
- Each CLI Program should be in it's own folder within the [cli](../cli/) folder
    - The CLI folder contains: a module folder for each program, `docker-entrypoint.sh`, and `slicer_cli_list.json`
        - Each module folder includes: `Module.py`, `Module.groovy`, and `Module.xml` files
            - QuPath uses [Groovy](https://groovy-lang.org/) for scripting. Groovy is not supported in Slicer CLI. The program script is thus written in Groovy.
            - The Python file is the bridge between Groovy and Slicer.
            - The XML file should be to DSA specifications. The [DSA's HistomicsTK](https://github.com/DigitalSlideArchive/HistomicsTK) XML files were used as examples/templates.
        - The programs listed in `slicer_cli_list.json` will be the ones avaliable for use on the DSA, any modules not listed won't appear.

## TissueDetect