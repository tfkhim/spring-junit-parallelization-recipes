# This file is part of junit-parallelization-recipes
#
# Copyright (c) 2026 Thomas Himmelstoss
#
# This software is subject to the MIT license. You should have
# received a copy of the license along with this program.

{
  description = "Dependencies for the spring-junit-parallelization-recipes project";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
  };

  outputs =
    { nixpkgs, ... }:
    let
      supportedSystems = [ "x86_64-linux" ];

      forSupportedSystems =
        generator:
        let
          generateForSystem =
            system:
            generator rec {
              inherit system;
              pkgs = nixpkgs.legacyPackages.${system};
            };
        in
        nixpkgs.lib.genAttrs supportedSystems generateForSystem;
    in
    {

      devShells = forSupportedSystems (
        { system, pkgs, ... }:
        let
          jdk = pkgs.jdk21;
        in
        {
          default = pkgs.mkShell {
            packages = [ jdk ];

            JAVA_HOME = "${jdk}/lib/openjdk";
          };
        }
      );

      formatter = forSupportedSystems (
        { pkgs, ... }:
        let
          pkgs = nixpkgs.legacyPackages.x86_64-linux;
        in
        pkgs.treefmt.withConfig {
          runtimeInputs = with pkgs; [
            nixfmt
            ktlint
          ];

          settings.formatter = {
            nixfmt = {
              command = "nixfmt";
              includes = [ "*.nix" ];
            };

            ktlint = {
              command = "ktlint";
              options = [ "--format" ];
              includes = [
                "*.kt"
                "*.kts"
              ];
            };
          };
        }
      );
    };
}
