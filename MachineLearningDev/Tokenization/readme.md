# Post Analysis and Evaluation

## Overview

This document presents the evaluation of three different URL tokenization methods tested after conducting research on URL analysis techniques. The goal was to identify the most effective tokenization method for use in phishing detection models, with a focus on execution time, token volume, and practicality in real-world applications.

### Tokenization Methods Tested:

1. **Regex-Based Tokenization**:
   - **Execution Time**: 0.4337 seconds (slowest)
   - **Token Volume**: Highest (554,009 tokens)
   - **Average Tokens per URL**: 6.69
   - **Pros**: Highly granular, capturing every component of the URL.
   - **Cons**: Slow execution and over-segmentation may hinder model performance on large datasets.

2. **TLD & Subdomain Tokenization**:
   - **Execution Time**: 0.2846 seconds (fastest)
   - **Token Volume**: Moderate (224,977 tokens)
   - **Average Tokens per URL**: 2.70
   - **Pros**: Balances tokenization depth and speed, focusing on key URL components like TLDs and subdomains.
   - **Cons**: May overlook deeper segments in URLs, which could hold critical information.

3. **Custom String Matching Tokenization**:
   - **Execution Time**: 0.2886 seconds
   - **Token Volume**: Lowest (14,823 tokens)
   - **Average Tokens per URL**: 0.18
   - **Pros**: Efficient in detecting specific patterns such as brand names.
   - **Cons**: Too specialized for general URL analysis, as it only captures predefined patterns.

## Recommendations

Based on the post-research testing, the **TLD & Subdomain Tokenization** method is recommended for general phishing detection tasks. It provides the best balance between speed, token relevance, and ease of use. However, in cases where more detailed tokenization is required, or for highly specialized tasks like brand detection, the **Custom String Matching Tokenization** method can be used alongside the TLD & Subdomain approach.

- For **large-scale phishing detection models**, **TLD & Subdomain Tokenization** is the preferred method, offering a good balance of performance and token detail.
- For **brand detection or specific URL pattern identification**, **Custom String Matching** tokenization can complement other tokenization methods.
- **Regex-Based Tokenization** is only recommended when highly granular URL analysis is needed, but it may introduce overhead in large datasets.

By optimizing the tokenization method based on the task at hand, we can ensure that our phishing detection models are both accurate and efficient.
