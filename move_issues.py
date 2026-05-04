import requests
import json

JIRA_URL = "https://rohinideviradhamanalan.atlassian.net/"
USERNAME = "rohinidevi.radhamanalan@gmail.com"
API_TOKEN = "ATATT3xFfGF0CNEfOzTCs5bWiOn45zJtfUQGhrUfV2CZQBwskqhe_bthMYjK_v8_Mn0FMxypAa6duspmnKFl9udeHbuN9w5J252IjhyPN5J54XZhDaIypvDxlXjYF99rFCZlWYy3KT2N4ckH5d5mTN0N00ZC54PhGkIqDHh2VkBKrqmc3-rxTLk=FBB2D91D"
auth = (USERNAME, API_TOKEN)

SOURCE_PROJECT = "KAN"
TARGET_PROJECT = "NEWS"

# First, let's check if NEWS project exists
response = requests.get(f"{JIRA_URL}/rest/api/3/project/{TARGET_PROJECT}", auth=auth)
print(f"Check NEWS project: {response.status_code}")
if response.status_code != 200:
    print(f"ERROR: NEWS project not found - {response.text}")
    raise SystemExit
target_project_data = response.json()
print(f"Target project: {target_project_data['name']}")
print(f"Issue types available: {[it['name'] for it in target_project_data['issueTypes']]}")

# Get all epics from KAN project
jql = f'project = {SOURCE_PROJECT} AND type = Epic ORDER BY key'
response = requests.get(f"{JIRA_URL}/rest/api/3/search/jql", auth=auth, params={"query": jql, "maxResults": 100})
print(f"\nSearch epics in {SOURCE_PROJECT}: {response.status_code}")
if response.status_code != 200:
    print(f"ERROR: Failed to search epics - {response.text}")
    raise SystemExit

epics = response.json()['values']
print(f"Found {len(epics)} epics in {SOURCE_PROJECT}")

# Get all tasks that are linked to epics
jql_tasks = f'project = {SOURCE_PROJECT} AND type = Task ORDER BY key'
response = requests.get(f"{JIRA_URL}/rest/api/3/search/jql", auth=auth, params={"query": jql_tasks, "maxResults": 100})
print(f"Search tasks in {SOURCE_PROJECT}: {response.status_code}")
if response.status_code != 200:
    print(f"ERROR: Failed to search tasks - {response.text}")
    raise SystemExit

tasks = response.json()['values']
print(f"Found {len(tasks)} tasks in {SOURCE_PROJECT}")

# Create a mapping of old issue keys to new issue keys after moving
issue_key_mapping = {}

# Move all epics first
print("\n=== Moving Epics ===")
for epic in epics:
    old_key = epic['key']
    issue_data = {
        "fields": {
            "project": {"key": TARGET_PROJECT}
        }
    }
    response = requests.put(f"{JIRA_URL}/rest/api/3/issue/{old_key}", auth=auth, json=issue_data)
    if response.status_code == 204:
        print(f"Moved epic: {old_key}")
        # Retrieve the updated issue to get the new key
        response = requests.get(f"{JIRA_URL}/rest/api/3/issue/{old_key}", auth=auth)
        if response.status_code == 200:
            new_key = response.json()['key']
            issue_key_mapping[old_key] = new_key
            print(f"  New key: {new_key}")
    else:
        print(f"Failed to move epic {old_key}: {response.status_code} - {response.text}")

# Move all tasks
print("\n=== Moving Tasks ===")
for task in tasks:
    old_key = task['key']
    parent_key = None
    
    # Check if task has a parent (epic)
    if 'parent' in task['fields'] and task['fields']['parent']:
        parent_key = task['fields']['parent']['key']
    
    issue_data = {
        "fields": {
            "project": {"key": TARGET_PROJECT}
        }
    }
    
    response = requests.put(f"{JIRA_URL}/rest/api/3/issue/{old_key}", auth=auth, json=issue_data)
    if response.status_code == 204:
        print(f"Moved task: {old_key}")
        # Retrieve the updated issue to get the new key
        response = requests.get(f"{JIRA_URL}/rest/api/3/issue/{old_key}", auth=auth)
        if response.status_code == 200:
            new_key = response.json()['key']
            issue_key_mapping[old_key] = new_key
            print(f"  New key: {new_key}")
    else:
        print(f"Failed to move task {old_key}: {response.status_code} - {response.text}")

# Re-link parent-child relationships with new keys
print("\n=== Re-linking Tasks to Epics ===")
for task in tasks:
    old_key = task['key']
    new_key = issue_key_mapping.get(old_key)
    
    if not new_key:
        continue
    
    parent_key = None
    if 'parent' in task['fields'] and task['fields']['parent']:
        parent_key = task['fields']['parent']['key']
    
    if parent_key and parent_key in issue_key_mapping:
        new_parent_key = issue_key_mapping[parent_key]
        issue_data = {
            "fields": {
                "parent": {"key": new_parent_key}
            }
        }
        response = requests.put(f"{JIRA_URL}/rest/api/3/issue/{new_key}", auth=auth, json=issue_data)
        if response.status_code == 204:
            print(f"Re-linked {new_key} -> {new_parent_key}")
        else:
            print(f"Failed to re-link {new_key}: {response.status_code} - {response.text}")

print("\n=== Move Complete ===")
print(f"Mapping: {json.dumps(issue_key_mapping, indent=2)}")
