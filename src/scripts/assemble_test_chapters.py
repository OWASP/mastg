import re
import yaml
from pathlib import Path

def append_tests_as_subsections():
    base_document_path = Path('Document')
    base_tests_path = Path('tests')

    testing_chapters = [filename for filename in base_document_path.glob('0x0[56]*-Testing-*.md')]
    for testing_chapter_path in testing_chapters:
        with testing_chapter_path.open('r') as f:
            testing_chapter_content = f.read()
            chapter_metadata = next(yaml.safe_load_all(testing_chapter_content))

        chapter_tests_content = ""

        platform = chapter_metadata['platform']
        masvs_category = chapter_metadata['masvs_category']

        tests_path = base_tests_path / platform / masvs_category
        for test_file in tests_path.glob('*'):
            with test_file.open('r') as f:
                test_content = f.read()
                # Extract yaml frontmatter
                yaml_front = next(yaml.safe_load_all(test_content))
                # Extract title and masvs_id
                title = yaml_front['title']
                masvs_v1_id = yaml_front['masvs_v1_id']
                masvs_v2_id = yaml_front['masvs_v2_id']
                # Add title header to content
                chapter_tests_content += f"\n\n## {title}"
                # Add MASVS header to content
                chapter_tests_content += f"\n\n> **MASVS V1:** {', '.join(masvs_v1_id)}\n>\n> **MASVS V2:** {'N/A' if not masvs_v2_id else ', '.join(masvs_v2_id)}\n"
                # Remove yaml frontmatter from test content
                test_content = re.sub(r'---\n(.|\n)*?\n---\n', '', test_content)
                # Add one nesting level to all headers
                test_content = re.sub(r'^#', '##', test_content, flags=re.MULTILINE)

                chapter_tests_content += '\n' + test_content.strip()

        # Write the updated content to the file
        with testing_chapter_path.open('a') as f:
            content = chapter_tests_content + '\n'
            f.write(content)

append_tests_as_subsections()

