# Girder Package Description: setup.py 
from setuptools import setup, find_packages

with open("README.md", "r", encoding = "utf-8") as f:
    long_description = f.read()

setup(
    name = "qupath_dsa",
    version = "0.0.1",
    author = "Kayla Neal",
    author_email = "man162@pitt.edu",
    description = "Package for using QuPath pipelines in Python",
    long_description = long_description,
    long_description_content_type = "text/markdown",
    url = "",
    include_package_data = True,
    packages = find_packages(),
    install_requires = [

    ],
    entry_points = {[

    ]},
    classifiers = [

    ]
)