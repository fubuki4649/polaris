<div align="center">

# polaris

**polaris** is an AI powered automatic music tagging tool

[![Apache-2.0](https://img.shields.io/badge/license-Apache%202.0-green)](https://www.apache.org/licenses/LICENSE-2.0.txt)

</div>

### What It Does

It takes a youtube playlist link, downloads the tracks, and populates the metadata and album art using Google Gemini and the iTunes Public API


### Dependencies

- [yt-dlp](https://github.com/yt-dlp/yt-dlp)

### Usage

1. Obtain a Gemini API key, and export as `export GEMINI_API_KEY=your_gemini_key`
2. `amper run -- [youtube playlist link] [output directory]`