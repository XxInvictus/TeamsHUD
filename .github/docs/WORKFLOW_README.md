# Workflow Documentation

Comprehensive documentation for the TeamsHUDPlus CI/CD workflow system.

## Table of Contents

- [Overview](#overview)
- [Workflow Architecture](#workflow-architecture)
- [Quick Start](#quick-start)
- [Documentation](#documentation)
- [Key Features](#key-features)
- [Workflow Jobs](#workflow-jobs)
- [Reusable Components](#reusable-components)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## Overview

The TeamsHUDPlus project uses GitHub Actions for continuous integration and deployment. The workflow system automatically builds, tests, and validates the mod across multiple modloaders (Fabric and Forge) and deployment targets (client, server, datagen).

### Design Principles

- **Modularity**: Reusable composite actions for common tasks
- **Flexibility**: Easy to extend with new modloaders and Minecraft versions
- **Maintainability**: Centralized logic with minimal duplication
- **Debuggability**: Conditional debug output via `DEBUG_MODE` variable
- **Extensibility**: JSON-based configuration for dependencies

## Workflow Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        dev-build.yml                        │
│                     (Main Workflow)                         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│   Build     │────▶│ Test Fabric  │────▶│   Cleanup   │
│             │     │              │     │             │
│ • Compile   │     │ • Client     │     │ • Remove    │
│ • Package   │     │ • Server     │     │   artifacts │
│ • Upload    │     │ • Datagen    │     └─────────────┘
│             │     └──────────────┘
│             │              │
│             │     ┌──────────────┐
│             │────▶│  Test Forge  │
└─────────────┘     │              │
                    │ • Client     │
                    │ • Server     │
                    │ • Datagen    │
                    └──────────────┘
```

### Component Structure

```
.github/
├── workflows/
│   └── dev-build.yml           # Main workflow definition
├── actions/
│   ├── prepare-test-env/       # Downloads artifacts, extracts properties
│   ├── check-mc-environment/   # Environment diagnostics (debug)
│   └── run-modloader-test/     # Comprehensive test orchestrator
└── scripts/
    ├── extract-properties.sh   # Property file parser
    ├── forge-remap.sh          # Forge-specific remapping
    └── validate-test-artifacts.sh  # Artifact validation
```

## Quick Start

### Prerequisites

- Self-hosted runner labeled `HomeServ` with:
  - Java 17+
  - Gradle
  - HeadlessMC 2.7.1+
  - Ubuntu Linux environment
- GitHub repository variables:
  - `DEBUG_MODE`: Set to `'true'` for diagnostic output

### Running the Workflow

1. **Automatic Trigger**: Push to `1.20.1-dev` branch
   ```bash
   git push origin 1.20.1-dev
   ```

2. **Manual Trigger**: Use GitHub Actions UI
   - Go to Actions → dev-build → Run workflow
   - Select branch `1.20.1-dev`

### Understanding Test Matrices

Tests run based on matrix configurations generated during build:

- **Fabric Matrix**: `["client", "server", "datagen"]`
- **Forge Matrix**: `["client", "server", "datagen"]`

Empty matrices (`[]`) skip testing for that modloader.

## Documentation

Detailed guides for specific aspects:

| Document | Description |
|----------|-------------|
| [EXTENDING_TESTS.md](EXTENDING_TESTS.md) | Adding new modloaders (NeoForge) and Minecraft versions |
| [DEPENDENCY_CONFIG.md](DEPENDENCY_CONFIG.md) | Configuring mod dependencies via JSON |

## Key Features

### 🔄 Multi-Modloader Support

- **Fabric**: Full support with automatic dependency downloads
- **Forge**: Full support with refmap validation and vanilla cleanup workaround
- **Extensible**: Easy to add NeoForge or other modloaders

### 🎯 Flexible Testing

- **Matrix-based**: Test multiple targets (client/server/datagen) independently
- **Conditional**: Skip tests via empty matrices
- **Isolated**: Each test runs in clean environment

### 🐛 Debug Mode

When `vars.DEBUG_MODE == 'true'`:
- Pre-flight environment checks
- Post-failure diagnostics
- Detailed file listings
- Regex validation output

### 📦 Dependency Management

- **Source Agnostic**: Modrinth, CurseForge, GitHub, direct URLs
- **JSON Configuration**: Define dependencies declaratively
- **Version Filtering**: Modloader and game version constraints
- **See**: [DEPENDENCY_CONFIG.md](DEPENDENCY_CONFIG.md)

### 🔧 Workarounds for Known Issues

1. **mc-runtime-test Vanilla Bug**
   - **Issue**: mc-runtime-test checks for vanilla version instead of modloader
   - **Solution**: Cleanup vanilla version directory before Forge tests
   - **Parameter**: `needs-vanilla-cleanup: 'true'`

2. **Hardcoded Path Issue**
   - **Issue**: mc-runtime-test uses hardcoded `/home/runner/.minecraft`
   - **Solution**: Symlink from actual `$HOME` to `/home/runner`
   - **Implementation**: Automatic in `run-modloader-test` action

## Workflow Jobs

### Build Job

**Purpose**: Compile and package mod for all modloaders

**Responsibilities**:
- Checkout repository
- Setup Java 17
- Run Gradle build
- Upload artifacts (fabric jar, forge jar, gradle properties)
- Generate test matrices
- Extract version information

**Outputs**:
- `minecraft_version`: Target Minecraft version
- `fabric_loader_version`: Fabric loader version
- `forge_version`: Forge version
- `fabric_matrix`: JSON array of Fabric test targets
- `forge_matrix`: JSON array of Forge test targets

**Artifacts**:
- `fabric-jar`: Fabric mod jar
- `forge-jar`: Forge mod jar
- `gradle-properties`: Build properties file

### Test Fabric Job

**Purpose**: Test Fabric builds in Minecraft runtime

**Strategy**: Matrix with targets from `fabric_matrix`

**Dependencies**:
- Downloads Fabric API from Modrinth
- Downloads Cloth Config from Modrinth

**Configuration**:
```yaml
modloader: fabric
test-regex: '.*fabric.*'
mc-runtime-test-type: fabric
needs-vanilla-cleanup: 'false'
download-dependencies: 'true'
```

### Test Forge Job

**Purpose**: Test Forge builds in Minecraft runtime

**Strategy**: Matrix with targets from `forge_matrix`

**Special Requirements**:
- Refmap validation (`--check-refmap` flag)
- Vanilla version cleanup (mc-runtime-test workaround)

**Configuration**:
```yaml
modloader: forge
test-regex: '.*forge.*'
mc-runtime-test-type: lexforge
needs-vanilla-cleanup: 'true'
validation-flags: '--check-refmap'
```

### Cleanup Job

**Purpose**: Remove temporary artifacts

**Runs**: Always, after all test jobs complete

**Actions**:
- Deletes uploaded artifacts (fabric-jar, forge-jar, gradle-properties)

## Reusable Components

### Composite Actions

#### prepare-test-env

**Location**: `.github/actions/prepare-test-env/`

**Purpose**: Prepare test environment by downloading artifacts and extracting properties

**Inputs**:
- `property-preset`: Configuration preset (fabric-test, forge-test, etc.)

**Outputs**:
- `minecraft_version`: Extracted Minecraft version
- `fabric_version`: Extracted Fabric version
- `fabric_loader_version`: Extracted Fabric loader version
- `forge_version`: Extracted Forge version
- `cloth_config_version`: Extracted Cloth Config version

**Steps**:
1. Download fabric-jar artifact
2. Download forge-jar artifact
3. Download gradle-properties artifact
4. Extract properties using preset

#### check-mc-environment

**Location**: `.github/actions/check-mc-environment/`

**Purpose**: Diagnostic checks for Minecraft test environment (debug only)

**Inputs**:
- `modloader`: Modloader identifier
- `modloader-regex`: Optional custom regex
- `check-refmap`: Whether to validate refmap files
- `mod-pattern`: Pattern for mod jars

**Checks**:
- Working directory contents
- Build artifacts in `build/libs/`
- Test artifacts in `run/mods/`
- Minecraft installation paths
- HeadlessMC installation
- Version regex matching

**Output**: Visual indicators (✓/⚠/✗) for each check

#### run-modloader-test

**Location**: `.github/actions/run-modloader-test/`

**Purpose**: Comprehensive test orchestrator for any modloader

**Inputs**: 13 parameters covering all test scenarios

**Key Parameters**:
- `modloader`: Modloader type
- `minecraft-version`: MC version
- `dependencies-config`: JSON dependency array
- `needs-vanilla-cleanup`: Forge workaround toggle
- `download-dependencies`: Dependency download toggle

**Steps**:
1. Fix Minecraft path (symlink for mc-runtime-test)
2. Prepare test environment
3. Download dependencies (conditional)
4. Validate artifacts
5. Pre-flight check (DEBUG_MODE)
6. Clean vanilla version (conditional)
7. Run mc-runtime-test
8. Post-failure check (DEBUG_MODE, on failure)

**See**: [EXTENDING_TESTS.md](EXTENDING_TESTS.md) for full parameter reference

### Scripts

#### extract-properties.sh

**Location**: `.github/scripts/extract-properties.sh`

**Purpose**: Parse gradle.properties with preset configurations

**Presets**:
- `fabric-test`: Extracts Fabric-specific versions
- `forge-test`: Extracts Forge-specific versions
- Custom presets: Define as needed

**Usage**:
```bash
./extract-properties.sh fabric-test
```

#### validate-test-artifacts.sh

**Location**: `.github/scripts/validate-test-artifacts.sh`

**Purpose**: Validate and prepare mod jars for testing

**Arguments**:
- `--modloader`: Required, specifies modloader type
- `--check-refmap`: Optional, validates refmap files

**Actions**:
- Creates `run/mods/` directory
- Copies matching jars from `build/libs/`
- Excludes javadoc, sources, dev jars
- Validates jar existence
- Checks refmap (if requested)

**Usage**:
```bash
./validate-test-artifacts.sh --modloader fabric
./validate-test-artifacts.sh --modloader forge --check-refmap
```

#### forge-remap.sh

**Location**: `.github/scripts/forge-remap.sh`

**Purpose**: Forge-specific remapping operations

## Configuration

### Repository Variables

Set in repository settings → Secrets and variables → Actions → Variables:

| Variable | Type | Description |
|----------|------|-------------|
| `DEBUG_MODE` | String | Set to `'true'` to enable diagnostic output |

### Matrix Configuration

Matrices are generated dynamically in the build job's "Determine Test Targets" step:

```bash
# Generate matrices based on build success or other conditions
if [ "${{ job.status }}" == "success" ]; then
  fabric_targets='["client", "server", "datagen"]'
  forge_targets='["client", "server", "datagen"]'
else
  fabric_targets='[]'
  forge_targets='[]'
fi

echo "fabric=$fabric_targets" >> $GITHUB_OUTPUT
echo "forge=$forge_targets" >> $GITHUB_OUTPUT
```

To disable a modloader test: Output empty array `[]`

### Dependency Configuration

Define runtime dependencies via JSON in workflow file:

```yaml
dependencies-config: |
  [
    {
      "name": "fabric-api",
      "source": "modrinth",
      "project_id": "P7dR8mSH",
      "version": "0.95.4+1.20.1",
      "loaders": ["fabric"],
      "game_versions": ["1.20.1"]
    }
  ]
```

**See**: [DEPENDENCY_CONFIG.md](DEPENDENCY_CONFIG.md) for complete guide

## Troubleshooting

### Common Issues

#### Forge Tests Fail with "Couldn't find object for regex"

**Symptoms**: Fabric tests pass, Forge tests fail with regex not matching

**Cause**: mc-runtime-test bug - checks vanilla version instead of modloader version

**Solution**: Ensure `needs-vanilla-cleanup: 'true'` for Forge tests

**Details**: When Fabric runs first, it installs vanilla 1.20.1. Forge test then sees vanilla exists and skips Forge installation, causing HeadlessMC to fail finding Forge version.

#### Tests Run on Wrong Runner

**Symptoms**: Tests run on GitHub-hosted runners instead of HomeServ

**Cause**: Runner label mismatch in workflow

**Solution**: Verify `runs-on: HomeServ` in job definition

#### Dependencies Not Downloaded

**Symptoms**: Missing mod dependencies in test environment

**Cause**: `download-dependencies` not set to `'true'` or empty `dependencies-config`

**Solution**: 
```yaml
download-dependencies: 'true'
dependencies-config: |
  [{ ... }]  # Must not be empty array
```

#### Debug Output Not Showing

**Symptoms**: No pre-flight or post-failure checks visible

**Cause**: `DEBUG_MODE` variable not set or not set to `'true'`

**Solution**: Set repository variable `DEBUG_MODE` to `'true'`

#### Archive Steps Not Working

**Status**: Known issue - archive steps removed in workflow refactoring

**Reason**: Archive functionality did not work correctly with test setup

**Alternative**: Use `download-artifact` action in separate job if needed

### Debug Workflow

1. **Enable Debug Mode**:
   ```
   Repository Settings → Secrets and variables → Actions → Variables
   Add: DEBUG_MODE = 'true'
   ```

2. **Trigger Workflow**:
   - Push to 1.20.1-dev or run manually

3. **Check Pre-Flight Output**:
   - Look for "Pre-Flight Environment Check" step
   - Verify all paths show ✓ indicators

4. **Check Post-Failure Output** (if test fails):
   - Look for "Post-Failure Environment Check" step
   - Review file listings and regex matching

5. **Common Debug Checks**:
   - Working directory: Should show build outputs
   - `build/libs/`: Should contain mod jars
   - `run/mods/`: Should contain test artifacts
   - `.minecraft/versions/`: Should show installed versions
   - HeadlessMC: Should show installation path

### Getting Help

1. Check this documentation first
2. Review [EXTENDING_TESTS.md](EXTENDING_TESTS.md) for modloader-specific issues
3. Review [DEPENDENCY_CONFIG.md](DEPENDENCY_CONFIG.md) for dependency issues
4. Enable DEBUG_MODE and review diagnostic output
5. Check mc-runtime-test action logs for detailed error messages

## Contributing

### Adding New Modloaders

See [EXTENDING_TESTS.md](EXTENDING_TESTS.md) for complete guide.

**Quick Steps**:
1. Add matrix output in build job
2. Create test job using `run-modloader-test` action
3. Configure dependencies if needed
4. Update validation script if jar patterns differ
5. Update cleanup job dependencies

### Adding New Minecraft Versions

**Option 1**: Create separate workflow file (recommended for major versions)

**Option 2**: Extend matrices with version dimension (for patch versions)

See [EXTENDING_TESTS.md](EXTENDING_TESTS.md) for detailed examples.

### Improving Actions

When modifying composite actions:

1. **Maintain backward compatibility**: Don't remove inputs without deprecation
2. **Add optional parameters**: Use defaults for new features
3. **Document changes**: Update relevant documentation files
4. **Test thoroughly**: Run on both GitHub-hosted and self-hosted runners
5. **Update examples**: Keep EXTENDING_TESTS.md examples current

### Code Style

- **YAML**: 2-space indentation, string inputs quoted
- **Bash**: Use `set -euo pipefail`, quote variables, check command existence
- **Documentation**: Use markdown, include code examples, link related docs

## Architecture Decisions

### Why Composite Actions?

- **Reusability**: Same logic across Fabric/Forge/NeoForge tests
- **Maintainability**: Single place to update common functionality
- **Testability**: Can be versioned and tested independently
- **Flexibility**: Parameters allow customization per modloader

### Why JSON for Dependencies?

- **Declarative**: Clear intent, easy to review
- **Extensible**: Add new sources without code changes
- **Portable**: Works across projects, not TeamsHUDPlus-specific
- **Type-safe**: JSON schema validation possible

### Why Self-Hosted Runner?

- **Performance**: Faster builds with local caching
- **Resources**: HeadlessMC requires specific setup
- **Control**: Custom environment configuration
- **Cost**: No GitHub Actions minutes consumed

### Why Matrix Strategy?

- **Parallelization**: Run client/server/datagen tests simultaneously
- **Flexibility**: Easy to add/remove test targets
- **Clarity**: Clear separation of test scenarios
- **Control**: Can disable individual targets via matrix modification

## Version History

### Current Version (v3.0)

- JSON-based dependency configuration
- Comprehensive `run-modloader-test` composite action
- Full modloader extensibility
- Improved documentation

### v2.0 (December 2025)

- Created reusable composite actions
- Added debug mode functionality
- Implemented Forge vanilla cleanup workaround
- Reduced workflow duplication by ~80%

### v1.0 (Original)

- Basic Fabric and Forge testing
- Inline logic in workflow file
- Manual artifact handling

## License

This workflow documentation is part of the TeamsHUDPlus project. See [LICENSE.md](../../LICENSE.md) for details.
