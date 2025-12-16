# Adding New Modloaders or Minecraft Versions

This guide explains how to extend the test workflow for new modloaders (e.g., NeoForge) or Minecraft versions (e.g., 1.21.1).

## Adding a New Modloader (e.g., NeoForge)

### Step 1: Update Build Job Outputs

In `dev-build.yml`, add matrix output for the new modloader:

```yaml
jobs:
  build:
    outputs:
      # ... existing outputs ...
      neoforge_matrix: ${{ steps.test_matrix.outputs.neoforge }}
```

### Step 2: Update Test Matrix Generation

In the "Determine Test Targets" step, add the new modloader:

```yaml
- name: Determine Test Targets
  run: |
    # ... existing logic ...
    echo "neoforge=${target_json}" >> $GITHUB_OUTPUT
```

### Step 3: Add Test Job

Copy and customize one of the existing test jobs:

```yaml
test-neoforge:
  name: Test NeoForge (${{ matrix.target }})
  needs: build
  runs-on: HomeServ
  strategy:
    matrix:
      target: ${{ fromJson(needs.build.outputs.neoforge_matrix) }}
  if: ${{ needs.build.result == 'success' && (github.event_name != 'push' || github.ref == 'refs/heads/1.20.1-dev') && needs.build.outputs.neoforge_matrix != '[]' }}
  
  steps:
    - name: Checkout Repository
      uses: actions/checkout@v4
    
    - name: Run NeoForge Modloader Test
      uses: ./.github/actions/run-modloader-test
      with:
        modloader: neoforge
        minecraft-version: ${{ needs.build.outputs.minecraft_version }}
        property-preset: neoforge-test
        validation-flags: '--check-refmap'
        modloader-regex: '.*neoforge.*'
        test-regex: '.*neoforge.*'
        mc-runtime-test-type: neoforge
        needs-vanilla-cleanup: 'true'  # or 'false' depending on behavior
        download-dependencies: 'false'
```

### Step 4: Configure Dependencies (if needed)

If NeoForge requires runtime dependencies, add them to `dependencies-config`. See [DEPENDENCY_CONFIG.md](DEPENDENCY_CONFIG.md) for full documentation:

```yaml
dependencies-config: |
  [
    {
      "name": "neoforge-dependency",
      "source": "modrinth",
      "project_id": "XXXXX",
      "loaders": ["neoforge"],
      "game_versions": ["1.20.1"]
    }
  ]
download-dependencies: 'true'
```

### Step 5: Update Validation Script

If NeoForge has different jar patterns, add to `validate-test-artifacts.sh`:

```bash
case "$MODLOADER" in
  # ... existing cases ...
  neoforge)
    JAR_PATTERN="*-neoforge-*.jar"
    EXCLUDE_PATTERN="! -name '*-javadoc.jar' ! -name '*-sources.jar' ! -name '*-dev.jar'"
    ;;
```

### Step 6: Create Property Preset

Add `neoforge-test` preset to `extract-properties.sh` if needed.

### Step 6: Update Cleanup Job

Add the new test job to cleanup dependencies:

```yaml
cleanup:
  needs: [build, test-fabric, test-forge, test-neoforge]
```

## Adding a New Minecraft Version (e.g., 1.21.1)

### Option 1: Separate Workflow (Recommended)

Create a new workflow file: `.github/workflows/dev-build-1.21.1.yml`

Copy the existing workflow and update:
- Branch triggers: `- '1.21.1-dev'`
- Build paths if necessary
- All property presets to use `1.21.1` configuration

### Option 2: Matrix by Version (Advanced)

Extend the build job to support multiple versions:

```yaml
jobs:
  build:
    strategy:
      matrix:
        mc_version: ['1.20.1', '1.21.1']
    outputs:
      minecraft_version: ${{ matrix.mc_version }}
      # Generate separate matrices per version+modloader
      fabric_1_20_1_matrix: ${{ steps.test_matrix.outputs.fabric_1_20_1 }}
      forge_1_20_1_matrix: ${{ steps.test_matrix.outputs.forge_1_20_1 }}
      fabric_1_21_1_matrix: ${{ steps.test_matrix.outputs.fabric_1_21_1 }}
      forge_1_21_1_matrix: ${{ steps.test_matrix.outputs.forge_1_21_1 }}
```

Then create version-specific test jobs:

```yaml
test-fabric-1-21-1:
  name: Test Fabric 1.21.1 (${{ matrix.target }})
  needs: build
  strategy:
    matrix:
      target: ${{ fromJson(needs.build.outputs.fabric_1_21_1_matrix) }}
  
  steps:
    - uses: actions/checkout@v4
    - uses: ./.github/actions/run-modloader-test
      with:
        modloader: fabric
        minecraft-version: '1.21.1'
        property-preset: fabric-test-1.21.1
        # ... other parameters ...
```

## Composite Action Parameters Reference

The `run-modloader-test` action accepts these parameters:

| Parameter | Required | Description | Example |
|-----------|----------|-------------|---------|
| `modloader` | Yes | Modloader identifier | `fabric`, `forge`, `neoforge` |
| `minecraft-version` | Yes | Minecraft version to test | `1.20.1`, `1.21.1` |
| `property-preset` | Yes | Preset for extract-properties.sh | `fabric-test`, `forge-test` |
| `validation-flags` | No | Flags for validate script | `--check-refmap` |
| `modloader-regex` | No | Version directory regex | `.*forge.*` (auto-generated if omitted) |
| `test-regex` | Yes | Regex for mc-runtime-test | `.*fabric.*` |
| `mc-runtime-test-type` | Yes | Type for mc-runtime-test | `fabric`, `lexforge`, `neoforge` |
| `dependencies-config` | No | JSON array of dependencies to download | See [DEPENDENCY_CONFIG.md](DEPENDENCY_CONFIG.md) |
| `java-version` | No | Java version | `17` (default) |
| `hmc-version` | No | HeadlessMC version | `2.7.1` (default) |
| `headlessmc-command` | No | Additional HMC params | `-Dhmc.loglevel="DEBUG"` |
| `needs-vanilla-cleanup` | No | Clean vanilla MC version | `true`, `false` (default: `false`) |
| `download-dependencies` | No | Download modloader deps | `true`, `false` (default: `false`) |

## Testing Your Changes

1. **Local validation**: Check YAML syntax with `yamllint` or GitHub's workflow validator
2. **Branch testing**: Push to a test branch and trigger via workflow_dispatch
3. **Matrix testing**: Start with limited matrix (e.g., `["client"]`) before enabling full matrix

## Common Patterns

### NeoForge for 1.20.1
```yaml
- uses: ./.github/actions/run-modloader-test
  with:
    modloader: neoforge
    minecraft-version: '1.20.1'
    property-preset: neoforge-test
    validation-flags: '--check-refmap'
    test-regex: '.*neoforge.*'
    mc-runtime-test-type: neoforge
    needs-vanilla-cleanup: 'true'
```

### Fabric for 1.21.1
```yaml
- uses: ./.github/actions/run-modloader-test
  with:
    modloader: fabric
    minecraft-version: '1.21.1'
    property-preset: fabric-test-1.21.1
    test-regex: '.*fabric.*'
    mc-runtime-test-type: fabric
    dependencies-config: |
      [
        {
          "name": "fabric-api",
          "source": "modrinth",
          "project_id": "P7dR8mSH",
          "loaders": ["fabric"],
          "game_versions": ["1.21.1"]
        },
        {
          "name": "cloth-config",
          "source": "modrinth",
          "project_id": "9s6osm5g",
          "version": "16.0.0",
          "loaders": ["fabric"],
          "game_versions": ["1.21.1"]
        }
      ]
    download-dependencies: 'true'
```

### Custom Modloader
```yaml
- uses: ./.github/actions/run-modloader-test
  with:
    modloader: quilt
    minecraft-version: '1.20.1'
    property-preset: quilt-test
    test-regex: '.*quilt.*'
    mc-runtime-test-type: quilt
    download-dependencies: 'true'  # if it needs dependencies
```

## Troubleshooting

- **Tests not running**: Check matrix generation in "Determine Test Targets" step
- **Wrong artifacts**: Verify jar pattern in `validate-test-artifacts.sh`
- **Version mismatch**: Ensure property-preset returns correct Minecraft version
- **Missing dependencies**: Set `download-dependencies: 'true'` and add download logic to composite action
- **Regex not matching**: Check `.minecraft/versions/` directory names and adjust `modloader-regex`

## Best Practices

1. **Version isolation**: Use separate branches or workflows for major version differences
2. **Incremental rollout**: Test new modloaders on limited matrices first
3. **Documentation**: Update this guide when adding new patterns
4. **Consistency**: Follow naming conventions (e.g., `test-{modloader}`, `{modloader}-test` preset)
5. **Validation**: Always include refmap checks for release builds
