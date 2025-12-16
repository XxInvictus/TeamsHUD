# Dependency Configuration Guide

The `run-modloader-test` composite action supports flexible dependency management through a JSON configuration. This allows you to specify dependencies from multiple sources (Modrinth, CurseForge, GitHub, direct URLs) without modifying the action itself.

## Configuration Format

The `dependencies-config` input accepts a JSON array of dependency objects:

```json
[
  {
    "name": "dependency-name",
    "source": "modrinth",
    "project_id": "...",
    "version": "1.0.0",
    "loaders": ["fabric"],
    "game_versions": ["1.20.1"]
  }
]
```

## Supported Sources

### Modrinth

Download mods from [Modrinth](https://modrinth.com/).

**Required Fields:**
- `name`: Filename (without .jar extension)
- `source`: `"modrinth"`
- `project_id`: Modrinth project ID (found in URL or project page)

**Optional Fields:**
- `version`: Specific version number (e.g., `"0.95.4+1.20.1"`). If omitted, uses latest version
- `loaders`: Array of modloader filters (e.g., `["fabric"]`, `["forge"]`)
- `game_versions`: Array of Minecraft versions (e.g., `["1.20.1"]`)

**Example:**
```json
{
  "name": "fabric-api",
  "source": "modrinth",
  "project_id": "P7dR8mSH",
  "version": "0.95.4+1.20.1",
  "loaders": ["fabric"],
  "game_versions": ["1.20.1"]
}
```

**Finding Project ID:**
- Visit the mod page on Modrinth
- Check the URL: `https://modrinth.com/mod/{project_id}`
- Or scroll to "Technical information" section on the mod page

### CurseForge

Download mods from [CurseForge](https://www.curseforge.com/).

**Required Fields:**
- `name`: Filename (without .jar extension)
- `source`: `"curseforge"`
- `project_id`: CurseForge project ID
- `file_id`: Specific file ID from CurseForge

**Example:**
```json
{
  "name": "jei",
  "source": "curseforge",
  "project_id": "238222",
  "file_id": "4826860"
}
```

**Finding IDs:**
- Project ID: In the "About Project" section or URL
- File ID: Click on a specific file, check the URL or use CurseForge API

### GitHub Releases

Download mods from GitHub releases.

**Required Fields:**
- `name`: Filename (without .jar extension)
- `source`: `"github"`
- `repo`: Repository in format `owner/repo`

**Optional Fields:**
- `tag`: Release tag (default: `"latest"`)
- `asset_pattern`: Regex pattern to match asset filename (default: `".*\\.jar$"`)

**Example:**
```json
{
  "name": "example-mod",
  "source": "github",
  "repo": "ExampleAuthor/ExampleMod",
  "tag": "v1.0.0",
  "asset_pattern": ".*-fabric\\.jar$"
}
```

**Latest Release:**
```json
{
  "name": "example-mod",
  "source": "github",
  "repo": "ExampleAuthor/ExampleMod"
}
```

### Direct URL

Download from any direct URL.

**Required Fields:**
- `name`: Filename (without .jar extension)
- `source`: `"url"`
- `url`: Direct download URL

**Example:**
```json
{
  "name": "custom-lib",
  "source": "url",
  "url": "https://example.com/downloads/custom-lib-1.0.0.jar"
}
```

## Usage Examples

### Fabric Dependencies

```yaml
- uses: ./.github/actions/run-modloader-test
  with:
    modloader: fabric
    minecraft-version: '1.20.1'
    property-preset: fabric-test
    dependencies-config: |
      [
        {
          "name": "fabric-api",
          "source": "modrinth",
          "project_id": "P7dR8mSH",
          "version": "0.95.4+1.20.1",
          "loaders": ["fabric"],
          "game_versions": ["1.20.1"]
        },
        {
          "name": "cloth-config",
          "source": "modrinth",
          "project_id": "9s6osm5g",
          "version": "15.0.0",
          "loaders": ["fabric"],
          "game_versions": ["1.20.1"]
        }
      ]
    download-dependencies: 'true'
```

### No Dependencies (Forge)

```yaml
- uses: ./.github/actions/run-modloader-test
  with:
    modloader: forge
    minecraft-version: '1.20.1'
    property-preset: forge-test
    dependencies-config: '[]'
    download-dependencies: 'false'
```

### Mixed Sources

```yaml
dependencies-config: |
  [
    {
      "name": "fabric-api",
      "source": "modrinth",
      "project_id": "P7dR8mSH",
      "loaders": ["fabric"],
      "game_versions": ["1.20.1"]
    },
    {
      "name": "jei",
      "source": "curseforge",
      "project_id": "238222",
      "file_id": "4826860"
    },
    {
      "name": "custom-mod",
      "source": "github",
      "repo": "Author/CustomMod",
      "tag": "v2.1.0"
    },
    {
      "name": "special-lib",
      "source": "url",
      "url": "https://cdn.example.com/special-lib.jar"
    }
  ]
```

### Using Dynamic Values

You can use GitHub Actions expressions in the JSON:

```yaml
dependencies-config: |
  [
    {
      "name": "fabric-api",
      "source": "modrinth",
      "project_id": "P7dR8mSH",
      "version": "${{ needs.build.outputs.fabric_api_version }}",
      "loaders": ["fabric"],
      "game_versions": ["${{ needs.build.outputs.minecraft_version }}"]
    }
  ]
```

## Version Matching Behavior

### Modrinth Version Matching

When specifying a version for Modrinth dependencies:

1. **Exact match**: First tries to find exact version match
2. **Prefix match**: If no exact match, tries versions starting with the specified version
3. **Latest**: If no version specified, uses latest version

Examples:
- `"version": "0.95.4+1.20.1"` → Finds exactly `0.95.4+1.20.1`
- `"version": "0.95.4"` → Finds `0.95.4+1.20.1`, `0.95.4+fabric`, etc.
- No version field → Uses latest available version

### GitHub Tag Matching

- `"tag": "latest"` or omitted → Uses latest release
- `"tag": "v1.0.0"` → Uses specific tagged release

## Error Handling

The dependency downloader will fail fast on errors:

- **Missing dependency**: If a dependency cannot be found, the workflow will exit with error
- **Network failures**: If API requests fail, the workflow will exit with error
- **Invalid configuration**: If required fields are missing, the workflow will exit with error

All downloaded files are placed in `run/mods/` with the name specified in the `name` field plus `.jar` extension.

## Common Modrinth Project IDs

| Mod | Project ID |
|-----|------------|
| Fabric API | P7dR8mSH |
| Cloth Config | 9s6osm5g |
| Mod Menu | mOgUt4GM |
| Sodium | AANobbMI |
| Lithium | gvQqBUqZ |
| Phosphor | hEOCdOgW |

Find more at [modrinth.com](https://modrinth.com/).

## Troubleshooting

### "Could not find version X"

- Verify the version number exactly matches a published version
- Check that loaders and game_versions filters match available versions
- Try omitting the version field to use latest

### "Failed to fetch from API"

- Check internet connectivity on the runner
- Verify project_id is correct
- For GitHub, ensure the repository is public or credentials are provided

### "Unknown dependency source"

- Verify `source` field is one of: `modrinth`, `curseforge`, `url`, `github`
- Check for typos in the source field

### Empty dependencies

If you don't need dependencies:
```yaml
dependencies-config: '[]'
download-dependencies: 'false'
```

## Advanced: Creating Reusable Configs

You can define dependencies in workflow variables for reuse:

```yaml
env:
  FABRIC_DEPS: |
    [
      {
        "name": "fabric-api",
        "source": "modrinth",
        "project_id": "P7dR8mSH",
        "loaders": ["fabric"],
        "game_versions": ["1.20.1"]
      }
    ]

jobs:
  test:
    steps:
      - uses: ./.github/actions/run-modloader-test
        with:
          dependencies-config: ${{ env.FABRIC_DEPS }}
```

Or use build outputs:

```yaml
jobs:
  build:
    outputs:
      fabric_dependencies: ${{ steps.config.outputs.deps }}
    steps:
      - id: config
        run: |
          cat > deps.json << 'EOF'
          [
            { "name": "fabric-api", "source": "modrinth", "project_id": "P7dR8mSH" }
          ]
          EOF
          echo "deps=$(cat deps.json | jq -c .)" >> $GITHUB_OUTPUT
  
  test:
    needs: build
    steps:
      - uses: ./.github/actions/run-modloader-test
        with:
          dependencies-config: ${{ needs.build.outputs.fabric_dependencies }}
```
