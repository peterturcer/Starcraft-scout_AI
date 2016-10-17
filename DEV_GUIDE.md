# BWU-Java Development Guide

This guide serves as a mandatory protocol for every collaborator for **BWU-Java**
to follow.

## Git repository

*	``master`` branch has to contain only fully tested and production ready code. You can merge only ``development`` to the ``master`` here.
*	``development`` branch is the main branch every collaborator should start with. Nevertheless, only quick bugfixes or small features should be directly developed on this branch. Any bigger feature / bugfix should be devleoped on a branched ``development``.
*	As often as it is possible you should keep track with your parent branch. For example, when you diverged from the ``development`` branch for a feature development that takes more than one day, you should merge the ``development`` branch to your branch every day for example. It is recommended to do this when you open your project for development.

## Keeping code clean

*	Eclipse IDE is recommended for development, *but not mandatory*.
*	Every new written code has to use the coding standard defined in ``code_standards`` directory. This is easily done with the Eclipse IDE (by importing the coding standard to the project), but can be adapted to other IDEs also.

## Development and refactoring

*	You are free to develop new features as you wish.
*	When changing or refactoring old features, **always discuss** that with the responsible person beforehand. It is also recommended to discuss that with all the persons using that feature.
*	More the feature or part of a project is used across the project, more advantages should the change / refactoring have before actually doing it.
