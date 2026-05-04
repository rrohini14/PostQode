import requests
import json

JIRA_URL = 'https://rohinideviradhamanalan.atlassian.net/'
USERNAME = 'rohinidevi.radhamanalan@gmail.com'
API_TOKEN = 'ATATT3xFfGF0CNEfOzTCs5bWiOn45zJtfUQGhrUfV2CZQBwskqhe_bthMYjK_v8_Mn0FMxypAa6duspmnKFl9udeHbuN9w5J252IjhyPN5J54XZhDaIypvDxlXjYF99rFCZlWYy3KT2N4ckH5d5mTN0N00ZC54PhGkIqDHh2VkBKrqmc3-rxTLk=FBB2D91D'
auth = (USERNAME, API_TOKEN)
PROJECT_KEY = 'KAN'
meta_url = f"{JIRA_URL}/rest/api/3/issue/createmeta?projectKeys={PROJECT_KEY}&issuetypeNames=Story&expand=projects.issuetypes.fields"
response = requests.get(meta_url, auth=auth)
print(response.status_code)
if response.status_code != 200:
    print(response.text)
    raise SystemExit
meta = response.json()
print(json.dumps(meta, indent=2)[:5000])
for proj in meta.get('projects', []):
    for it in proj.get('issuetypes', []):
        print('Issue Type:', it['name'])
        for fid, finfo in it.get('fields', {}).items():
            if 'epic' in (finfo.get('name') or '').lower() or 'parent' in (finfo.get('name') or '').lower():
                print(fid, finfo.get('name'), 'required=', finfo.get('required'))
