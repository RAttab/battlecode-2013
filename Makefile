#------------------------------------------------------------------------------#
# Makefile
# Rémi Attab (remi.attab@gmail.com), 11 Jan 2013
# FreeBSD-style copyright and disclaimer apply
#
# Makefile to setup and run our project.
#------------------------------------------------------------------------------#

INSTALL_DIR ?= ~/Battlecode2013

BOTS := team216 \
	testplayer \
	rusher

install: $(foreach bot,$(BOTS),$(INSTALL_DIR)/teams/$(bot))

$(INSTALL_DIR)/teams/%:
	 ln -s $(CURDIR)/$* $@

#------------------------------------------------------------------------------#
# TRAINING SETUP
#------------------------------------------------------------------------------#

# generate the template and seed file for the genetic algorithm.

clean-template:
	rm weights.tpl genome.txt

template: clean-template
	cat team216/Weights.java | \
	sed -r 's/([_A-Z]+)\s*=\s*(-?[0-9]*\.?[0-9]*);/\1 = %(\1)f;/' > weights.tpl
	cat team216/Weights.java | \
	egrep '=\s*(-?[0-9]*\.?[0-9]*);' | \
	sed -r 's/.*\s([_A-Z]+)\s*=\s*(-?[0-9]*\.?[0-9]*);.*/\1=\2/' > genome.txt


# Setup an agent folder for each member of the population.

TEAM_DIR := $(INSTALL_DIR)/teams
BIN_DIR := $(INSTALL_DIR)/bin
MAX_POP := 100

clean-train:
	bash -c 'rm -rf $(TEAM_DIR)/ga_{0..$(MAX_POP)}'
	bash -c 'rm -rf $(BIN_DIR)/ga_{0..$(MAX_POP)}'

install-train: template clean-train
	bash -c 'mkdir $(TEAM_DIR)/ga_{0..$(MAX_POP)}'
	bash -c 'echo $(TEAM_DIR)/ga_{0..$(MAX_POP)} | xargs -n1 cp team216/*'
	bash -c 'rm $(TEAM_DIR)/ga_{0..$(MAX_POP)}/Weights.java'

clean: clean-template clean-trean
