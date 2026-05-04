import requests
import json

JIRA_URL = 'https://rohinideviradhamanalan.atlassian.net/'
USERNAME = 'rohinidevi.radhamanalan@gmail.com'
API_TOKEN = 'ATATT3xFfGF0CNEfOzTCs5bWiOn45zJtfUQGhrUfV2CZQBwskqhe_bthMYjK_v8_Mn0FMxypAa6duspmnKFl9udeHbuN9w5J252IjhyPN5J54XZhDaIypvDxlXjYF99rFCZlWYy3KT2N4ckH5d5mTN0N00ZC54PhGkIqDHh2VkBKrqmc3-rxTLk=FBB2D91D'
auth = (USERNAME, API_TOKEN)
response = requests.get(f"{JIRA_URL}/rest/api/3/field", auth=auth)
print(response.status_code)
if response.status_code != 200:
    print(response.text)
    raise SystemExit
fields = response.json()
for field in fields:
    if 'epic' in field.get('name', '').lower():
        print(field)
