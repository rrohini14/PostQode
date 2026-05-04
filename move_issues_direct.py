import requests

JIRA_URL = "https://rohinideviradhamanalan.atlassian.net/"
USERNAME = "rohinidevi.radhamanalan@gmail.com"
API_TOKEN = "ATATT3xFfGF0CNEfOzTCs5bWiOn45zJtfUQGhrUfV2CZQBwskqhe_bthMYjK_v8_Mn0FMxypAa6duspmnKFl9udeHbuN9w5J252IjhyPN5J54XZhDaIypvDxlXjYF99rFCZlWYy3KT2N4ckH5d5mTN0N00ZC54PhGkIqDHh2VkBKrqmc3-rxTLk=FBB2D91D"
auth = (USERNAME, API_TOKEN)

SOURCE_PROJECT = "KAN"
TARGET_PROJECT = "NEWS"

# Check if NEWS project exists and get its info
response = requests.get(f"{JIRA_URL}/rest/api/3/project/{TARGET_PROJECT}", auth=auth)
print(f"Check NEWS project: {response.status_code}")
if response.status_code != 200:
    print(f"ERROR: NEWS project not found - {response.text}")
    raise SystemExit
target_project_data = response.json()
print(f"Target project: {target_project_data['name']}")
print(f"Issue types available: {[it['name'] for it in target_project_data['issueTypes']]}")

# Known epics and tasks from the creation script
epics = ["KAN-1", "KAN-2", "KAN-3", "KAN-4", "KAN-5"]
tasks = [f"KAN-{i}" for i in range(6, 56)]

# Map of task to parent epic
task_to_parent = {}
for i, epic in enumerate(epics):
    epic_index = i
    start = epic_index * 10 + 6
    end = start + 10
    for j in range(start, end):
        task_to_parent[f"KAN-{j}"] = epic

print(f"\nTask to Parent mapping:")
for task, parent in list(task_to_parent.items())[:5]:
    print(f"  {task} -> {parent}")
print(f"  ... and {len(task_to_parent) - 5} more")

issue_key_mapping = {}

# Move all epics first
print("\n=== Moving Epics ===")
for epic_key in epics:
    issue_data = {
        "fields": {
            "project": {"key": TARGET_PROJECT}
        }
    }
    response = requests.put(f"{JIRA_URL}/rest/api/3/issue/{epic_key}", auth=auth, json=issue_data)
    if response.status_code == 204:
        print(f"Moved epic: {epic_key}")
        # Retrieve the updated issue to get the new key
        response = requests.get(f"{JIRA_URL}/rest/api/3/issue/{epic_key}", auth=auth)
        if response.status_code == 200:
            new_key = response.json()['key']
            issue_key_mapping[epic_key] = new_key
            print(f"  New key: {new_key}")
    else:
        print(f"WARN: Failed to move epic {epic_key}: {response.status_code} - {response.text[:200]}")

# Move all tasks
print("\n=== Moving Tasks ===")
for task_key in tasks:
    issue_data = {
        "fields": {
            "project": {"key": TARGET_PROJECT}
        }
    }
    response = requests.put(f"{JIRA_URL}/rest/api/3/issue/{task_key}", auth=auth, json=issue_data)
    if response.status_code == 204:
        print(f"Moved task: {task_key}")
        # Retrieve the updated issue to get the new key
        response = requests.get(f"{JIRA_URL}/rest/api/3/issue/{task_key}", auth=auth)
        if response.status_code == 200:
            new_key = response.json()['key']
            issue_key_mapping[task_key] = new_key
            print(f"  New key: {new_key}")
    else:
        print(f"WARN: Failed to move task {task_key}: {response.status_code} - {response.text[:200]}")

# Re-link parent-child relationships with new keys
print("\n=== Re-linking Tasks to Epics ===")
for old_task_key in tasks:
    new_task_key = issue_key_mapping.get(old_task_key)
    old_parent_key = task_to_parent.get(old_task_key)
    
    if not new_task_key or not old_parent_key:
        continue
    
    new_parent_key = issue_key_mapping.get(old_parent_key)
    
    if not new_parent_key:
        print(f"WARN: Could not find new parent key for {old_parent_key}")
        continue
    
    issue_data = {
        "fields": {
            "parent": {"key": new_parent_key}
        }
    }
    response = requests.put(f"{JIRA_URL}/rest/api/3/issue/{new_task_key}", auth=auth, json=issue_data)
    if response.status_code == 204:
        print(f"Re-linked {new_task_key} -> {new_parent_key}")
    else:
        print(f"WARN: Failed to re-link {new_task_key}: {response.status_code} - {response.text[:200]}")

print("\n=== Move Complete ===")
print(f"\nEpic mapping:")
for old, new in sorted(issue_key_mapping.items()):
    if old in epics:
        print(f"  {old} -> {new}")
print(f"\nTask sample mapping (first 5):")
for i, (old, new) in enumerate(sorted(issue_key_mapping.items())):
    if old not in epics and i < 5:
        print(f"  {old} -> {new}")
