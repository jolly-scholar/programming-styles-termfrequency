import sys
import numpy as np

# Arrays-
#   Using Python+numpy, or any other array programming language,
#   implement a program that takes as input an array of characters (the characters from Pride and Prejudice, for example),
#   normalizes to UPPERCASE, ignores words smaller than 2 characters,
#   and replaces the vowels with their Leet counterparts when there is a one-to-one mapping.
#   Then it prints out the 5 most frequently occurring 2-grams.
#
# reference: https://github.com/crista/exercises-in-programming-style/blob/master/03-arrays/tf-03.py

# Vowels
vowels = {"A",
          "E",
          "I",
          "O",
          "U"
}

# Leet replacement counterparts
leet = {
    "A": "4",
    "B": "8",
    "C": "¢",
    "D": "|}",
    "E": "£",
    "F": "|=",
    "G": "6",
    "H": "#",
    "I": "1",
    "J": "_/",
    "K": "|<",
    "L": "|_",
    "M": "44",
    "N": "И",
    "O": "0",
    "P": "|7",
    "Q": "9",
    "R": "Я",
    "S": "$",
    "T": "7",
    "U": "[_]",
    "V": "\/",
    "W": "\/\/",
    "X": "%",
    "Y": "¥",
    "Z": "2",
}

# function for replacement
def leet_replace(ch):
    if ch in vowels:
        return leet.get(ch)
    else:
        return ch or ''

# Load TXT and import characters into the array
characters = np.array([' '] + list(open(sys.argv[1]).read()) + [' '])

# Normalize: Replace non-alphabets with spaces and capitalize all characters
characters[~np.char.isalpha(characters)] = ' '
characters = np.char.upper(characters)

# Replace the vowels with their Leet counterparts
# using vectorize method to perform leet_replace to each elements, element-wise
characters = np.vectorize(leet_replace)(characters)

# Split the words by finding the indices of spaces
sp = np.where(characters == ' ')

# Double each index for word split, and then take pairs
sp2 = np.repeat(sp, 2)

# Get the pairs as a 2D matrix, skip the first and the last
# First, remove first and last char from the array
sp2 = sp2[1:-1]
# reshape sp2 array into 2 colums per row
w_ranges = np.reshape(sp2, (-1, 2))

# Remove the indexing to the spaces themselves
# By checking the difference between the consecutive indexes and making sure its greater bigger than 2.
w_ranges = w_ranges[np.where(w_ranges[:, 1] - w_ranges[:, 0] > 2)]

# Voila! Words are in between spaces, given as pairs of indices
# using lamda to extract all characters from start to end index
words = list(map(lambda r: characters[r[0]:r[1]], w_ranges))

# Let's recode the characters as strings
# strip spaces and join the characters
swords = np.array(list(map(lambda w: ''.join(w).strip(), words)))

# Next, let's remove stop words
sw = np.array([' '] + list(open('../stop_words.txt').read()) + [' '])
sw = np.char.upper(sw)
sw = np.vectorize(leet_replace)(sw)
stop_words_string = ''.join(sw)
stop_words_array = np.array(list(set(stop_words_string.split(','))))
ns_words = swords[~np.isin(swords, stop_words_array)]

# TWO-GRAMS:
# Applying the same technique used above to generate two-gram array.
# sword array will be doubled, first and last will be removed, and the two grams will be stored
ns_words = np.repeat(ns_words, 2)
# skip first and last words
ns_words = ns_words[1:-1]

# Generate two grams: repeated_word_strings[1:-1]: skip the first and the last
two_words = np.reshape(ns_words, (-1, 2))

# Let's recode the two words as a two-gram
two_grams = np.array(list(map(lambda w: ' '.join(w).strip(), two_words)))

# Finally, count the two gram occurrences
uniq, counts = np.unique(two_grams, axis=0, return_counts=True)
twogramf_sorted = sorted(zip(uniq, counts), key=lambda t: t[1], reverse=True)

# print
print("---------- two grams (top5) stop_words removed -----------");
for two_gram, count in twogramf_sorted[:5]:
    print(two_gram, '-', count)
