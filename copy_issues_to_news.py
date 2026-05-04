import requests
import json

JIRA_URL = "https://rohinideviradhamanalan.atlassian.net/"
USERNAME = "rohinidevi.radhamanalan@gmail.com"
API_TOKEN = "ATATT3xFfGF0CNEfOzTCs5bWiOn45zJtfUQGhrUfV2CZQBwskqhe_bthMYjK_v8_Mn0FMxypAa6duspmnKFl9udeHbuN9w5J252IjhyPN5J54XZhDaIypvDxlXjYF99rFCZlWYy3KT2N4ckH5d5mTN0N00ZC54PhGkIqDHh2VkBKrqmc3-rxTLk=FBB2D91D"
auth = (USERNAME, API_TOKEN)

SOURCE_PROJECT = "KAN"
TARGET_PROJECT = "NEWS"

print("=== Retrieving all issues from KAN project ===")
# Get all epics and tasks in order
epics_data = []
for i in range(1, 6):
    issue_key = f"KAN-{i}"
    response = requests.get(f"{JIRA_URL}/rest/api/3/issue/{issue_key}", auth=auth)
    if response.status_code == 200:
        epics_data.append(response.json())
        print(f"Retrieved {issue_key}")

tasks_data = []
for i in range(6, 56):
    issue_key = f"KAN-{i}"
    response = requests.get(f"{JIRA_URL}/rest/api/3/issue/{issue_key}", auth=auth)
    if response.status_code == 200:
        tasks_data.append(response.json())
        print(f"Retrieved {issue_key}")

print(f"\nTotal: {len(epics_data)} epics, {len(tasks_data)} tasks")

# Create mapping of old keys to new keys
old_to_new = {}

print("\n=== Creating epics in NEWS project ===")
for epic in epics_data:
    old_key = epic['key']
    summary = epic['fields']['summary']
    description = epic['fields'].get('description')
    
    issue_data = {
        "fields": {
            "project": {"key": TARGET_PROJECT},
            "summary": summary,
            "issuetype": {"name": "Epic"}
        }
    }
    
    if description:
        if isinstance(description, str):
            issue_data["fields"]["description"] = {
                "type": "doc",
                "version": 1,
                "content": [
                    {
                        "type": "paragraph",
                        "content": [{"type": "text", "text": description}]
                    }
                ]
            }
        else:
            issue_data["fields"]["description"] = description
    
    response = requests.post(f"{JIRA_URL}/rest/api/3/issue", auth=auth, json=issue_data)
    if response.status_code == 201:
        new_key = response.json()['key']
        old_to_new[old_key] = new_key
        print(f"Created {new_key} from {old_key}")
    else:
        print(f"Failed to create epic from {old_key}: {response.status_code} - {response.text[:200]}")

print("\n=== Creating tasks in NEWS project ===")
for task in tasks_data:
    old_key = task['key']
    summary = task['fields']['summary']
    description = task['fields'].get('description')
    old_parent = task['fields'].get('parent')
    
    parent_key = None
    if old_parent:
        old_parent_key = old_parent['key']
        parent_key = old_to_new.get(old_parent_key)
    
    issue_data = {
        "fields": {
            "project": {"key": TARGET_PROJECT},
            "summary": summary,
            "issuetype": {"name": "Task"}
        }
    }
    
    if description:
        if isinstance(description, str):
            issue_data["fields"]["description"] = {
                "type": "doc",
                "version": 1,
                "content": [
                    {
                        "type": "paragraph",
                        "content": [{"type": "text", "text": description}]
                    }
                ]
            }
        else:
            issue_data["fields"]["description"] = description
    
    if parent_key:
        issue_data["fields"]["parent"] = {"key": parent_key}
    
    response = requests.post(f"{JIRA_URL}/rest/api/3/issue", auth=auth, json=issue_data)
    if response.status_code == 201:
        new_key = response.json()['key']
        old_to_new[old_key] = new_key
        print(f"Created {new_key} from {old_key}" + (f" (parent: {parent_key})" if parent_key else ""))
    else:
        print(f"Failed to create task from {old_key}: {response.status_code} - {response.text[:200]}")

print("\n=== Summary ===")
print(f"Created {len([k for k in old_to_new if k.startswith('KAN-1')])+ len([k for k in old_to_new if k.startswith('KAN-2')]) + len([k for k in old_to_new if k.startswith('KAN-3')]) + len([k for k in old_to_new if k.startswith('KAN-4')]) + len([k for k in old_to_new if k.startswith('KAN-5')])} new issues in NEWS project")
print(f"\nNew issue keys:")
for old, new in sorted(old_to_new.items()):
    if old in [f"KAN-{i}" for i in range(1, 6)]:
        print(f"  Epic {old} -> {new}")

print("\n=== Issue Key Mapping ===")
print(json.dumps(old_to_new, indent=2)[:1000] + "...")

print("\n=== Deleting old issues from KAN project ===")
delete_count = 0
for old_key in list(old_to_new.keys()):
    response = requests.delete(f"{JIRA_URL}/rest/api/3/issue/{old_key}?deleteSubtasks=true", auth=auth)
    if response.status_code == 204:
        print(f"Deleted {old_key}")
        delete_count += 1
    else:
        print(f"Failed to delete {old_key}: {response.status_code}")

print(f"\nDeleted {delete_count} issues from KAN project")
