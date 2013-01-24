#------------------------------------------------------------------------------#
# Makefile
# Rémi Attab (remi.attab@gmail.com), 11 Jan 2013
# FreeBSD-style copyright and disclaimer apply
#
# Makefile to setup and run our project.
#------------------------------------------------------------------------------#

OS := $(shell uname)

ifeq ($(OS), Darwin) # Mac OS X
	INSTALL_DIR ?= /Applications/Battlecode2013
else
	INSTALL_DIR ?= ~/Battlecode2013
endif

#INSTALL_DIR := ./tmp

TEAM_DIR := $(INSTALL_DIR)/teams
BIN_DIR := $(INSTALL_DIR)/bin

BOTS := team216 \
	rusher \
	godotbot \
	nuclear \
	bobot

install-bots: $(foreach bot,$(BOTS),$(INSTALL_DIR)/teams/$(bot))

$(INSTALL_DIR)/teams/%:
	 ln -s $(CURDIR)/$* $@

#------------------------------------------------------------------------------#
# TRAINING SETUP
#------------------------------------------------------------------------------#

# generate the template and seed file for the genetic algorithm.

clean-template:
	-rm weights.tpl genome.txt

template: clean-template
	-mkdir weights # trained weights are stored here.
	cat team216/Weights.java | \
	sed -r 's/double\s+([_A-Z]+)\s*=\s*(-?[0-9]*\.?[0-9]*);/double \1 = %(\1)f;/' | \
	sed -r 's/package (team216);/package %(team)s;/' > weights.tpl
	cat team216/Weights.java | \
	egrep 'double.*=\s*(-?[0-9]*\.?[0-9]*);' | \
	sed -r 's/.*double\s+([_A-Z]+)\s*=\s*(-?[0-9]*\.?[0-9]*);.*/\1=\2/' > genome.txt


# Setup an agent folder for each member of the population.

MAX_POP := 30
GA_DIR := ga_{0..$(MAX_POP)}

clean-train:
	bash -c 'rm -rf $(TEAM_DIR)/$(GA_DIR)'
	bash -c 'rm -rf $(BIN_DIR)/$(GA_DIR)'

install-train: install-bots template clean-train
	bash -c 'mkdir $(TEAM_DIR)/$(GA_DIR)'
	bash -c 'echo $(TEAM_DIR)/$(GA_DIR) | xargs -n1 cp team216/*'
	bash -c 'echo $(TEAM_DIR)/$(GA_DIR)' | xargs -n1 ./rename-packages
# bash -c 'rm $(TEAM_DIR)/$(GA_DIR)/Weights.java'

clean: clean-template clean-train

