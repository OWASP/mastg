import os
import re
from pathlib import Path
from typing import List
import yaml
from dataclasses import dataclass
import git_data

EMOJIS_regex = r"🥇 |🎁 |📝 |❗ "

@dataclass
class MarkdownLink:
    raw: str
    text: str
    url: str
    external: bool
    title: str = ""
    raw_new: str = ""


def remove_emojis(file_text):
    print("[*] Regex Substitutions for emojis")
    found = re.findall(EMOJIS_regex, file_text)
    print(f"    Found: {found}")
    return re.sub(EMOJIS_regex, r"", file_text)


def extract_markdown_links(md_file_content: str) -> List[MarkdownLink]:
    """
    Extracts markdown links from the given content.
    """
    markdown_links = []
    link_pattern = r'\[([^\]]+)\]\(([^ ]+)( "([^"]+)")?\)\)?'

    for match in re.finditer(link_pattern, md_file_content):
        raw, text, url = match.group(0), match.group(1).strip('"').strip('**'), match.group(2)
        title = match.group(4) if len(match.groups()) > 2 else None
        title = title.replace("\\","") if title else None
        
        if url.startswith("#"):
            continue
        if external:=url.startswith("http"):
            raw_new = construct_external_link(raw, url, text, title)
        else:
            raw_new = construct_internal_link(raw, url, text, title)
        markdown_links.append(MarkdownLink(raw, text, url, external, title, raw_new))

    return markdown_links


def construct_internal_link(raw, url, text, title):
    """
    Constructs a new internal link with the correct directory.
    """
    full_url = f"/MASTG/{get_directory_from_code(raw)}" if "0x" in raw else ""
    title = f' "{title}"' if title else ""

    return f"[{text}]({full_url}{title})".replace(".md", "")

def construct_external_link(raw, url, text, title):
    """
    Constructs a new internal link with the correct directory.
    """
    text = title if title else text
    title = f' "{title}"' if title else ""

    return f"[{text}]({url}{title})"

def get_directory_from_code(raw):
    """
    Maps directory code to directory name.
    """
    match = re.search(r'0x(\d{2})', raw)
    if match:
        directory = {
            "01": "Intro",
            "02": "Intro",
            "03": "Intro",
            "04": "General",
            "05": "Android",
            "06": "iOS",
            "08": "Tools",
            "09": "Intro",
        }.get(match.group(1))
        relative_path = raw[match.start():-1]
        return f"{directory}/{relative_path}"
    else:
        return ""


def get_links_from_anchor(links, anchor):
    """
    Extracts specific links from the anchor text.
    """
    return sorted(set([
        re.search(r"\#([^ \")]*)(?=\s|\)|$)", link.raw).group(1)
        for link in links
        if not link.external and "#" in link.raw and anchor in link.raw
    ]))


def update_yaml_frontmatter(file_text, tools, examples, external_links, last_updated):
    """
    Updates the YAML frontmatter with the tools and examples list.
    """
    # Regular expression to match the YAML frontmatter at the beginning of the file
    frontmatter_pattern = r'^---\n(.*?)\n---\n'
    match = re.search(frontmatter_pattern, file_text, re.DOTALL)
    
    if match:
        frontmatter_str = match.group(1)
        frontmatter = yaml.safe_load(frontmatter_str)

        # Update the tools and examples in the frontmatter
        frontmatter["tools"] = update_frontmatter_list(frontmatter.get("tools", []), tools)
        frontmatter["examples"] = update_frontmatter_list(frontmatter.get("examples", []), examples)

        # update with external links
        frontmatter["resources"] = update_frontmatter_list(frontmatter.get("external_links", []), external_links)

        frontmatter["last_updated"] = last_updated

        # Replace the old frontmatter with the updated frontmatter
        updated_frontmatter = f"---\n{yaml.dump(frontmatter, indent=4, sort_keys=False)}---\n"
        file_text = file_text.replace(match.group(0), updated_frontmatter, 1)

    return file_text


def update_frontmatter_list(current_list, new_items):
    """
    Updates a list in the frontmatter with new items.
    """
    updated_list = current_list + new_items
    return sorted(list(set(updated_list)))


def links_to_markdown(links, title):
    """
    Converts a list of links to markdown.
    """
    section = ""
    if len(links) > 0:
        links = sorted(list(set([link.raw_new for link in links])))      
        links_text = "\n".join([f"- {link}" for link in links])
        # TODO links_text = f"\n\n### {title}\n\n{links_text}"
        links_text = f"\n\n{links_text}"
        section += links_text
    return section


def split_links(links):
    internal_links = [link for link in links if link.external is False]
    external_links = [link for link in links if link.external is True]

    return internal_links, external_links

def update_internal_links(file_text, links):
    new_text = file_text
    for link in links:        
        new_text = new_text.replace(link.raw, link.raw_new)
    return new_text

def create_resources_section(internal_links, external_links):
    # TODO internal_links_section = links_to_markdown(internal_links, "Internal")
    external_links_section = links_to_markdown(external_links, "External")
    # TODO resources_section = internal_links_section + external_links_section
    resources_section = external_links_section

    if resources_section != "":
        resources_section = "\n\n## Resources" + resources_section + "\n"
    return resources_section

def process_markdown_files(folder):
    """
    Processes all markdown files in the given folder.
    """
    for root, _, filenames in os.walk(folder):
        if filenames:
            markdown_files = Path(root).glob('*.md')

            for markdown_file in markdown_files:
                if markdown_file.name == "index.md":
                    continue
                file_content = markdown_file.read_text()
                links = extract_markdown_links(file_content)
                
                internal_links, external_links = split_links(links)

                tools = get_links_from_anchor(internal_links, "0x08a")
                examples = get_links_from_anchor(internal_links, "0x08b")

                resources_section = create_resources_section(internal_links, external_links)

                file_content = update_internal_links(file_content, internal_links)

                external_links = [link.url for link in external_links]

                last_updated = git_data.get_last_commit_date(Path(markdown_file.as_posix().replace('docs/MASTG', '.')).absolute().as_posix())

                updated_content = update_yaml_frontmatter(file_content, tools, examples, external_links, last_updated)
                markdown_file.write_text(updated_content + resources_section)


process_markdown_files("docs/MASTG")