from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
from nltk import word_tokenize          
from nltk.stem import WordNetLemmatizer
import nltk
from nltk.corpus import stopwords
import sys, os

#devnull=open(os.devnull, "w")
#sys.stderr = devnull
#sys.stdout= devnull

# Download stopwords list
nltk.download('punkt',quiet=True)
nltk.download('wordnet',quiet=True)
nltk.download('stopwords',quiet=True)
nltk.download('omw-1.4',quiet=True)
stop_words = set(stopwords.words('english')) 

# Interface lemma tokenizer from nltk with sklearn
class LemmaTokenizer:
    ignore_tokens = [',', '.', ';', ':', '"', '``', "''", '`','_']
    def __init__(self):
        self.wnl = WordNetLemmatizer()
    def __call__(self, doc):
        return [self.wnl.lemmatize(t) for t in word_tokenize(doc) if t not in self.ignore_tokens]

# Lemmatize the stop words
tokenizer=LemmaTokenizer()
token_stop = tokenizer(' '.join(stop_words))

#items in cache
search_terms = 'temparature of s1 temparature of s2 temparature of s2 temparature of s2'

#whole dataset in cache and payload
documents = ['temparature of s1','temparature of s2 = 1', 'temparature of s3 = 98','temparature of s4 = 56','z of s5 = 56']

# Create TF-idf model
vectorizer = TfidfVectorizer(stop_words=token_stop, 
                              tokenizer=tokenizer)
doc_vectors = vectorizer.fit_transform([search_terms] + documents)

# Calculate similarity
cosine_similarities = linear_kernel(doc_vectors[0:1], doc_vectors).flatten()
#for x in cosine_similarities:
#    print(x)
document_scores = [item.item() for item in cosine_similarities[1:]]
print(document_scores)
